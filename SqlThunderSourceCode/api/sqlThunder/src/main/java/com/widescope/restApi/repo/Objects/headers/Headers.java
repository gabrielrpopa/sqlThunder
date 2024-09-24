/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.widescope.restApi.repo.Objects.headers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


public class Headers {
	private List<Header> listOfHeaders = null;
	public Headers() {
		this.setListOfHeaders(new ArrayList<Header>());
	}
	
	public Headers(final List<Header> listOfHeaders) {
		this.setListOfHeaders(listOfHeaders);
	}

	public List<Header> getListOfHeaders() { return listOfHeaders; }
	public void setListOfHeaders(List<Header> listOfHeaders) { this.listOfHeaders = listOfHeaders; }
	public void addHeaders(Header header) { this.listOfHeaders.add(header); }

	public void populate() {
		List<HeaderValue> headerValuesACCEPT = new ArrayList<HeaderValue>();
		headerValuesACCEPT.add(new HeaderValue(1,"*/*"));
		headerValuesACCEPT.add(new HeaderValue(2,"application/javascript, */*;q=0.8"));
		headerValuesACCEPT.add(new HeaderValue(3,"application/json"));
		headerValuesACCEPT.add(new HeaderValue(4,"application/xml"));
		headerValuesACCEPT.add(new HeaderValue(5,"application/xml,application/xhtml+xml,text/html;q=0.9, text/plain;q=0.8,image/png,*/*;q=0.5'"));
		headerValuesACCEPT.add(new HeaderValue(6,"audio/webm, audio/ogg, audio/wav, audio/*;q=0.9, application/ogg;q=0.7, video/*;q=0.6;*/*;q=0.5'"));
		headerValuesACCEPT.add(new HeaderValue(7,"audio/webm,audio/ogg,audio/wav,audio/*;q=0.9,application/ogg;q=0.7,video/*;q=0.6,*/*;q=0.5"));
		headerValuesACCEPT.add(new HeaderValue(8,"image/jpeg, application/x-ms-application, image/gif, application/xaml+xml, image/pjpeg, application/x-ms-xbap, application/x-shockwave-flash, application/msword, */*"));
		headerValuesACCEPT.add(new HeaderValue(9,"image/png,image/svg+xml,image/*;q=0.8, */*;q=0.5"));
		headerValuesACCEPT.add(new HeaderValue(10,"image/webp,image/*,*/*;q=0.8"));
		headerValuesACCEPT.add(new HeaderValue(11,"text/css,*/*;q=0.1"));
		headerValuesACCEPT.add(new HeaderValue(12,"text/html, application/xhtml+xml, image/jxr, */*"));
		headerValuesACCEPT.add(new HeaderValue(13,"text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1"));
		headerValuesACCEPT.add(new HeaderValue(14,"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		headerValuesACCEPT.add(new HeaderValue(15,"text/plain"));
		this.listOfHeaders.add(new Header(1, "ACCEPT", headerValuesACCEPT) );
		
		
		List<HeaderValue> headerValuesACCEPTCHARSET = new ArrayList<HeaderValue>();
		headerValuesACCEPTCHARSET.add(new HeaderValue(16,"utf-8"));
		this.listOfHeaders.add(new Header(2, "ACCEPT-CHARSET", headerValuesACCEPTCHARSET) ) ;
		
		List<HeaderValue> headerValuesACCEPTENCODING = new ArrayList<HeaderValue>();
		headerValuesACCEPTENCODING.add(new HeaderValue(17,"compress"));
		headerValuesACCEPTENCODING.add(new HeaderValue(18,"deflate"));
		headerValuesACCEPTENCODING.add(new HeaderValue(19,"gzip"));
		this.listOfHeaders.add(new Header(3, "ACCEPT-ENCODING", headerValuesACCEPTENCODING)) ;
		
		List<HeaderValue> headerValuesACCEPTLANGUAGE = new ArrayList<HeaderValue>();
		headerValuesACCEPTLANGUAGE.add(new HeaderValue(20,"en-GB, en;q=0.5"));
		headerValuesACCEPTLANGUAGE.add(new HeaderValue(21,"en-US"));
		headerValuesACCEPTLANGUAGE.add(new HeaderValue(22,"es"));
		headerValuesACCEPTLANGUAGE.add(new HeaderValue(23,"hin"));
		headerValuesACCEPTLANGUAGE.add(new HeaderValue(24,"jpn"));
		headerValuesACCEPTLANGUAGE.add(new HeaderValue(25,"ru"));
		headerValuesACCEPTLANGUAGE.add(new HeaderValue(26,"zh-CN"));
		this.listOfHeaders.add(new Header(4, "ACCEPT-LANGUAGE", headerValuesACCEPTLANGUAGE)) ;
		

		List<HeaderValue> headerValuesAUTHORIZATION = new ArrayList<HeaderValue>();
		this.listOfHeaders.add(new Header(5, "AUTHORIZATION", headerValuesAUTHORIZATION)) ;

		
		
		List<HeaderValue> headerValuesCACHECONTROL = new ArrayList<HeaderValue>();
		headerValuesCACHECONTROL.add(new HeaderValue(27,"max-age=3600"));
		headerValuesCACHECONTROL.add(new HeaderValue(28,"max-stale"));
		headerValuesCACHECONTROL.add(new HeaderValue(29,"min-fresh=3600"));
		headerValuesCACHECONTROL.add(new HeaderValue(30,"no-cache"));
		headerValuesAUTHORIZATION.add(new HeaderValue(31,"no-store"));
		headerValuesCACHECONTROL.add(new HeaderValue(32,"no-transform"));
		headerValuesCACHECONTROL.add(new HeaderValue(33,"only-if-cached"));
		this.listOfHeaders.add(new Header(6, "CACHE-CONTROL", headerValuesCACHECONTROL));
		
		List<HeaderValue> headerValuesCONNECTION = new ArrayList<HeaderValue>();
		headerValuesCONNECTION.add(new HeaderValue(34,"close"));
		headerValuesCONNECTION.add(new HeaderValue(35,"keep-alive"));
		this.listOfHeaders.add(new Header(7, "CONNECTION", headerValuesCONNECTION)) ;
		
		this.listOfHeaders.add(new Header(8, "CONTENT-LENGTH", new ArrayList<HeaderValue>())) ; // no values
		
		
		List<HeaderValue> headerValuesCONTENTTYPE = new ArrayList<HeaderValue>();
		headerValuesCONTENTTYPE.add(new HeaderValue(36,"application/atom+xml"));
		headerValuesCONTENTTYPE.add(new HeaderValue(36,"application/base64"));
		headerValuesCONTENTTYPE.add(new HeaderValue(37,"application/javascript"));
		headerValuesCONTENTTYPE.add(new HeaderValue(38,"application/json"));
		headerValuesCONTENTTYPE.add(new HeaderValue(39,"application/octet-stream"));
		headerValuesCONTENTTYPE.add(new HeaderValue(40,"application/x-www-form-urlencoded"));
		headerValuesCONTENTTYPE.add(new HeaderValue(41,"application/xml"));
		headerValuesCONTENTTYPE.add(new HeaderValue(41,"multipart/alternative"));
		headerValuesCONTENTTYPE.add(new HeaderValue(42,"multipart/form-data"));
		headerValuesCONTENTTYPE.add(new HeaderValue(43,"multipart/mixed"));
		headerValuesCONTENTTYPE.add(new HeaderValue(44,"text/css"));
		headerValuesCONTENTTYPE.add(new HeaderValue(45,"text/html"));
		headerValuesCONTENTTYPE.add(new HeaderValue(46,"text/plain"));
		this.listOfHeaders.add(new Header(9, "CONTENT-TYPE", headerValuesCONTENTTYPE));
			
		
		List<HeaderValue> headerValuesCookie = new ArrayList<HeaderValue>();
		headerValuesCookie.add(new HeaderValue(47,"name=value"));
		headerValuesCookie.add(new HeaderValue(48,"name=value; name2=value2; name3=value3"));
		this.listOfHeaders.add(new Header(10, "COOKIE", headerValuesCookie));
		
		this.listOfHeaders.add(new Header(11, "DATE", new ArrayList<HeaderValue>())) ; // no values
		
		List<HeaderValue> headerValuesExpect = new ArrayList<HeaderValue>();
		headerValuesExpect.add(new HeaderValue(49,"100-continue"));
		
		this.listOfHeaders.add(new Header(12, "EXPECT", headerValuesExpect)) ;
		
		this.listOfHeaders.add(new Header(13, "FROM", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(14, "HOST", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(15, "IF_MATCH", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(16, "IF-MODIFIED-SINCE", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(17, "IF-NONE-MATCH", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(18, "IF-RANGE", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(19, "IF-UNMODIFIED-SINCE", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(20, "MAX-FORWARD',", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(21, "PRAGMA", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(22, "PROXY-AUTHORIZATION", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(23, "RANGE", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(24, "REFERER", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(25, "TE", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(26, "UPGRADE", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(27, "USER-AGENT", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(28, "VIA", new ArrayList<HeaderValue>())) ;
		this.listOfHeaders.add(new Header(29, "WARNING", new ArrayList<HeaderValue>())) ;
		
	}



	@Override
	public String toString() {
		return new Gson().toJson(this);
	}


}
	
