<#if licenseFirst??>
<#if licenseFirst == "/*">
/* *****************************************************************************
<#else>
${licenseFirst}
</#if>
</#if>
${licensePrefix}Copyright (C) ${date?date?string("yyyy")}, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
${licensePrefix}All rights reserved.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix}Redistribution and use in source and binary forms, with or without
${licensePrefix}modification, are permitted provided that the following conditions are met:
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix}1. Redistributions of source code must retain the above copyright notice,
${licensePrefix}   this list of conditions and the following disclaimer.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix}2. Redistributions in binary form must reproduce the above copyright notice,
${licensePrefix}   this list of conditions and the following disclaimer in the documentation
${licensePrefix}   and/or other materials provided with the distribution.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix}3. Neither the name of the copyright holder nor the names of its contributors
${licensePrefix}   may be used to endorse or promote products derived from this software
${licensePrefix}   without specific prior written permission.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix}THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
${licensePrefix}AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
${licensePrefix}IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
${licensePrefix}ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
${licensePrefix}LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
${licensePrefix}CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
${licensePrefix}SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
${licensePrefix}INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
${licensePrefix}CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
${licensePrefix}ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
${licensePrefix}POSSIBILITY OF SUCH DAMAGE.
${licensePrefix?replace(" +$", "", "r")}
${licensePrefix}Contributors:
${licensePrefix}  Prasanth R. Pasala
${licensePrefix}  Brian E. Pangburn
${licensePrefix}  Diego Gil
${licensePrefix}  Man "Bee" Vo
${licensePrefix}  Ernie R. Rael
<#if licenseLast??>
<#if licenseLast == " */">
 * ****************************************************************************/
<#else>
${licenseLast}
</#if>
</#if>
