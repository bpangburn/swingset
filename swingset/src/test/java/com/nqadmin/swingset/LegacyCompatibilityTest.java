/*
 * Copyright (C) 2026, Brian E. Pangburn & The Pangburn Group
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.nqadmin.swingset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.text.ParseException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nqadmin.swingset.enums.YesNo;
import com.nqadmin.swingset.enums.Gender3;
import com.nqadmin.swingset.formatting.SSCurrencyField;
import com.nqadmin.swingset.formatting.SSDateField;
import com.nqadmin.swingset.formatting.SSFormattedTextField;
import com.nqadmin.swingset.formatting.SSIntegerField;
import com.nqadmin.swingset.formatting.SSNumericField;
import com.nqadmin.swingset.formatting.SSTimestampField;
import com.nqadmin.swingset.models.SSListItem;
import com.nqadmin.swingset.utils.SSEnums.Navigation;

import dev.visdb.seesaw.utils.LookupDefaults;
import dev.visdb.seesaw.utils.SsComponent;

class LegacyCompatibilityTest {
  @BeforeAll
  static void initializeSeeSawDefaults() {
    LookupDefaults.init();
  }

  @Test
  void comboBoxLegacyNamesDelegateToSeeSaw() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSComboBox combo = new SSComboBox();
      SSBaseComboBox<Integer, String, ?> base = combo;

      combo.setOptions(new String[] {"Zero", "One"}, new int[] {10, 20});
      base.setSelectedMapping(20);

      assertEquals(20, base.getSelectedMapping());
      assertEquals("One", base.getSelectedOption());

      combo.setOptions(YesNo.class);
      combo.setSelectedEnum(YesNo.YES);
      assertEquals(YesNo.YES, combo.getSelectedEnum());
    });
  }

  @Test
  void dbComboBoxLegacyNamesDelegateToSeeSaw() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSDBComboBox combo = new SSDBComboBox();
      SSBaseComboBox<Long, Object, Object> base = combo;

      combo.addOption("One", 1L);
      combo.addOption(Date.valueOf("2026-09-18"), Date.valueOf("2026-09-19"), 2L);
      combo.updateOption(1L, "Updated");
      base.setSelectedMapping(1L);

      assertEquals(1L, base.getSelectedMapping());
      assertEquals("Updated", base.getSelectedOption());
    });
  }

  @Test
  void listLegacyNamesDelegateToSeeSaw() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSList list = new SSList();
      list.setOptions(Arrays.asList("Eight", "Five"), Arrays.asList(8, 5));
      list.setSelectedValues(new Object[] {5});

      assertEquals(Arrays.asList(5), list.getSelectedMappings());
    });
  }

  @Test
  void formattedFieldsRetainTheLegacyBaseType() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      assertLegacyFormattedField(new SSCurrencyField());
      assertLegacyFormattedField(new SSDateField(SSDateField.DDMMYYYY));
      assertLegacyFormattedField(new SSNumericField());
      assertLegacyFormattedField(new SSIntegerField());
      assertLegacyFormattedField(new SSTimestampField());
    });
  }

  @Test
  void cellEditingRetainsTheLegacyCallbacksAndSeeSawType() {
    SSCellEditing editing = new SSCellEditing() {
      @Override
      public boolean cellUpdateRequested(int row, int column, Object oldValue, Object newValue) {
        return false;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    assertInstanceOf(dev.visdb.seesaw.table.SsCellEditing.class, editing);
    assertTrue(!editing.cellUpdateRequested(0, 0, null, null));
    assertTrue(!editing.isCellEditable(0, 0));
  }

  @Test
  void legacyNavigationCallbackReceivesSeeSawNavigation() {
    AtomicReference<Navigation> navigation = new AtomicReference<>();
    SSDBNav dbNav = new SSDBNav() {
      @Override
      public void performNavigationOps(Navigation navigationType) {
        navigation.set(navigationType);
      }
    };

    dbNav.performNavigationOps(dev.visdb.seesaw.navigate.RowsAction.Navigation.Next);

    assertEquals(Navigation.Next, navigation.get());
  }

  @Test
  void dbComboPreservesSelectionWhenSecondDisplayValueIsIntroduced() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSDBComboBox combo = new SSDBComboBox();
      combo.addOption("One", 1L);
      combo.setSelectedMapping(1L);
      combo.addOption("Two", "Extra", 2L);
      assertEquals(1L, combo.getSelectedMapping());
      assertEquals("One", combo.getSelectedOption());
      combo.setSelectedOption("Two");
      assertEquals(2L, combo.getSelectedMapping());
      assertEquals("Extra", combo.getChosenItem().getD2());
      combo.addOption("Three", 3L);
      combo.setSelectedMapping(3L);
      assertEquals("Three", combo.getSelectedOption());
      assertNull(combo.getChosenItem().getD2());
    });
  }

  @Test
  void dbComboPreservesNullOptionWhenSecondDisplayValueIsIntroduced() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      // Emulate the non-navigator mode of a bound foreign-key combo.
      SSDBComboBox combo = new SSDBComboBox() {
        @Override
        protected boolean isComboBoxNavigator() {
          return false;
        }
      };
      combo.setAllowNull(true);
      combo.addOption("One", 1L);
      combo.setSelectedMapping(null);
      combo.addOption("Two", "Extra", 2L);
      assertNull(combo.getSelectedMapping());
      assertEquals(3, combo.getItemCount(),
          "keys=" + combo.getKeys() + ", allowNull=" + combo.getAllowNull()
              + ", nullItem=" + combo.getNullItem());
      combo.setSelectedOption("Two");
      assertEquals(2L, combo.getSelectedMapping());
      combo.setSelectedOption(null);
      assertNull(combo.getSelectedMapping());
    });
  }

  @Test
  void comboSupportsBothModelsAndNullMappings() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      for (boolean useGlazedLists : new boolean[] {false, true}) {
        SSComboBox combo = new SSComboBox(useGlazedLists);
        combo.setAllowNull(true);
        combo.setOptions(new String[] {"One", "Two"}, new int[] {5, 10});
        combo.setSelectedOption("Two");
        assertEquals(10, combo.getSelectedMapping());
        combo.setSelectedMapping(null);
        assertNull(combo.getSelectedMapping());
        combo.setOptions(Gender3.class);
        combo.setSelectedEnum(Gender3.UNISEX);
        assertEquals(2, combo.getSelectedMapping());
        assertEquals("Unisex", combo.getSelectedOption());
        assertEquals(Gender3.UNISEX, combo.getSelectedEnum());
      }
    });
  }

  @Test
  void listArrayGetterReturnsMappingsAndSupportsMultipleSelection() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSList list = new SSList();
      list.setOptions(new String[] {"One", "Two", "Three"}, new Integer[] {5, 10, 15});
      list.setSelectedValues(new Object[] {5, 15});
      assertArrayEquals(new Object[] {5, 15}, list.getSelectedValues());
      assertEquals(Arrays.asList(5, 15), list.getSelectedMappings());
      list.setSelectedValues(new Object[0]);
      assertTrue(list.getSelectedMappings().isEmpty());
      assertArrayEquals(new Object[] {-1}, list.getSelectedValues());
    });
  }

  @Test
  void numericFieldsClearAccordingToNullability() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      for (SSFormattedTextField field : new SSFormattedTextField[] {
          new SSNumericField(), new SSCurrencyField()}) {
        field.setAllowNull(false);
        field.cleanField();
        assertEquals(0, ((Number) field.getValue()).intValue());
        assertTrue(field.componentValidate());
        field.setAllowNull(true);
        field.cleanField();
        assertNull(field.getValue());
        assertTrue(field.componentValidate());
        field.setAllowNull(false);
        assertFalse(field.componentValidate());
      }
    });
  }

  @Test
  void defaultNumericFieldsDoNotTruncateAmountsToThreeIntegerDigits() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      for (SSFormattedTextField field : new SSFormattedTextField[] {
          new SSNumericField(), new SSCurrencyField()}) {
        field.setValue(1234567.89);
        try {
          Number value = (Number) field.getFormatter().stringToValue(field.getText());
          assertEquals(1234567.89, value.doubleValue(), 0.001);
        } catch (ParseException exception) {
          throw new AssertionError(exception);
        }
      }
      SSNumericField numeric = new SSNumericField(9, 4);
      assertEquals(9, numeric.getPrecision());
      assertEquals(4, numeric.getDecimals());
      numeric.setDecimals(2);
      assertEquals(9, numeric.getPrecision());
      assertEquals(2, numeric.getDecimals());
      SSCurrencyField currency = new SSCurrencyField(12, 3);
      assertEquals(12, currency.getPrecision());
      assertEquals(3, currency.getDecimals());
    });
  }

  @Test
  void dateFormattersAcceptValidDatesAndRejectImpossibleDates() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      assertDateParsing(new SSDateField(SSDateField.MMDDYYYY), "10/30/2026", "02/31/2026");
      assertDateParsing(new SSDateField(SSDateField.DDMMYYYY), "30/10/2026", "31/02/2026");
      assertDateParsing(new SSTimestampField(),
          "2026-10-30 12:34:56.000 +00:00", "2026-02-31 12:34:56.000 +00:00");
    });
  }

  @Test
  void dateFormattersValidateWithDatabaseColumnTypes() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSDateField date = new SSDateField(SSDateField.DDMMYYYY) {
        @Override
        public java.sql.JDBCType getColumnJDBCType() {
          return java.sql.JDBCType.DATE;
        }
      };
      assertDateParsing(date, "30/10/2026", "31/02/2026");
      SSTimestampField timestamp = new SSTimestampField() {
        @Override
        public java.sql.JDBCType getColumnJDBCType() {
          return java.sql.JDBCType.TIMESTAMP;
        }
      };
      assertDateParsing(timestamp,
          "2026-10-30 12:34:56.000 +00:00", "2026-02-31 12:34:56.000 +00:00");
    });
  }

  private static void assertDateParsing(
      SSFormattedTextField field, String valid, String invalid) {
    try {
      assertInstanceOf(java.util.Date.class, field.getFormatter().stringToValue(valid));
    } catch (ParseException exception) {
      throw new AssertionError("Valid date rejected: " + valid, exception);
    }
    assertThrows(ParseException.class, () -> field.getFormatter().stringToValue(invalid));
  }

  @Test
  void legacyNavigationIntegerValuesAndInvalidValuesAreHandled() {
    AtomicReference<Navigation> navigation = new AtomicReference<>();
    SSDBNav dbNav = new SSDBNav() {
      @Override
      public void performNavigationOps(Navigation navigationType) {
        navigation.set(navigationType);
      }
    };
    for (Navigation expected : Navigation.values()) {
      dbNav.performNavigationOps(expected.getValue());
      assertEquals(expected, navigation.get());
    }
    assertThrows(IllegalArgumentException.class, () -> dbNav.performNavigationOps(-1));
  }

  @SuppressWarnings("unused")
  private static void acceptsLegacyCellEditing(SSDataGrid grid, SSCellEditing editing) {
    grid.setSSCellEditing(editing);
  }

  @SuppressWarnings({"unused", "null"})
  private static void coversReportedCompilerSignatures(
      SSComboBox combo, SSDBComboBox dbCombo, SSList list) {
    SSBaseComboBox<?, ?, ?> baseCombo = combo;
    SSListItem item = null;
    SSFormattedTextField.AbstractFormatter formatter = null;

    combo.setOptions(new String[] {"One"});
    combo.setOptions(new String[] {"One"}, new int[] {1});
    combo.setOptions(YesNo.class);
    combo.setSelectedMapping(null);
    combo.setSelectedEnum(YesNo.NO);
    combo.getSelectedMapping();
    combo.getSelectedOption();
    combo.getSelectedEnum();

    dbCombo.addOption("One", 1L);
    dbCombo.addOption(Date.valueOf("2026-09-18"), Date.valueOf("2026-09-19"), 2L);
    dbCombo.addOption(Date.valueOf("2026-09-18"), 3L);
    dbCombo.addOption(null, 4L);
    dbCombo.setSelectedMapping(null);
    dbCombo.setSelectedOption("One");
    dbCombo.getSelectedMapping();
    dbCombo.getSelectedOption();
    dbCombo.updateOption(1L, "Updated");

    list.setOptions(Arrays.asList("One"));
    list.setOptions(Arrays.asList("One"), Arrays.asList(1));
    list.setOptions(new String[] {"One"}, new Integer[] {1});
    list.setOptions(YesNo.class);
    list.setSelectedValues(new Object[] {1});
    list.getSelectedMappings();

    new SSFormattedTextField(formatter);
  }

  private static void assertLegacyFormattedField(SSFormattedTextField field) {
    assertInstanceOf(SsComponent.class, field);
  }

  @Test
  void legacyBaseComboExposesTheConcreteSwingEditor() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSComboBox combo = new SSComboBox();
      SSDBComboBox dbCombo = new SSDBComboBox();
      SSBaseComboBox<?, ?, ?> baseCombo = combo;
      SSBaseComboBox<?, ?, ?> baseDbCombo = dbCombo;
      org.junit.jupiter.api.Assertions.assertSame(combo.getEditor(), baseCombo.getEditor());
      org.junit.jupiter.api.Assertions.assertSame(dbCombo.getEditor(), baseDbCombo.getEditor());
      assertInstanceOf(java.awt.Component.class, baseCombo.getEditor().getEditorComponent());
      assertInstanceOf(java.awt.Component.class, baseDbCombo.getEditor().getEditorComponent());
    });
  }

  @Test
  void integerFieldRetainsLegacyConstructorsPrecisionAndNullHandling() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      SSIntegerField field = new SSIntegerField(9);
      assertEquals(9, field.getPrecision());
      field.setPrecision(12);
      assertEquals(12, field.getPrecision());
      assertEquals(javax.swing.SwingConstants.RIGHT, field.getHorizontalAlignment());

      SSIntegerField defaultField = new SSIntegerField();
      defaultField.setValue(1234567);
      try {
        Number value = (Number) defaultField.getFormatter().stringToValue(defaultField.getText());
        assertEquals(1234567, value.intValue());
      } catch (ParseException exception) {
        throw new AssertionError(exception);
      }

      field.setAllowNull(false);
      field.cleanField();
      assertEquals(0, ((Number) field.getValue()).intValue());
      assertTrue(field.componentValidate());
      field.setAllowNull(true);
      field.cleanField();
      assertNull(field.getValue());
      assertTrue(field.componentValidate());
      field.setAllowNull(false);
      assertFalse(field.componentValidate());

      javax.swing.text.NumberFormatter formatter = new javax.swing.text.NumberFormatter(
          java.text.NumberFormat.getIntegerInstance());
      javax.swing.text.DefaultFormatterFactory factory =
          new javax.swing.text.DefaultFormatterFactory(formatter);
      SSIntegerField customField = new SSIntegerField(factory);
      org.junit.jupiter.api.Assertions.assertSame(factory, customField.getFormatterFactory());
      assertLegacyFormattedField(customField);
    });
  }
}
