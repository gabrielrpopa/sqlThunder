package com.widescope.license;


import java.time.LocalDate;




public class License {

	
	public static String printLicense() {
		
		
		
		LocalDate currentDate = LocalDate.now();
		int currentYear = currentDate.getYear();
		String lic = "\r\n"
				+ "###############################################################################\r\n"
				+ "MIT License\r\n"
				+ "\r\n"
				/*+ "Copyright 	(c) @yearcompany@,  @company@ \r\n"*/
				+ "		(c) @yeardeveloper@,  @developer@ \r\n"
				+ "\r\n"
				+ "Permission is hereby granted, free of charge, to any person obtaining a copy\r\n"
				+ "of this software (the \"Software\"), to use it, and to permit persons to whom the Software is\r\n"
				+ "furnished to do so, subject to the following conditions:\r\n"
				+ "\r\n"
				+ "The above copyright notice and this permission notice shall be included in all\r\n"
				+ "copies of the Software.\r\n"
				+ "\r\n"
				+ "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\r\n"
				+ "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\r\n"
				+ "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\r\n"
				+ "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\r\n"
				+ "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\r\n"
				+ "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\r\n"
				+ "SOFTWARE.\r\n"
				+ "###############################################################################" ;
		//lic = new String( FileUtilWrapper.readFile("license.txt") );
		//lic = ResourseOperations.getFileContent("public/license.txt");
		lic = lic.replaceAll("@yearcompany@", String.valueOf(currentYear));
		lic = lic.replaceAll("@yeardeveloper@", String.valueOf(currentYear));
		lic = lic.replaceAll("@company@", "Infinit Loop Corp Limited");
		lic = lic.replaceAll("@developer@", "Gabriel R. Popa");
		return lic;
	}
	
	
}
