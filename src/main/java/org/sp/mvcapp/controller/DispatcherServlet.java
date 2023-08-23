package org.sp.mvcapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//웹 어플리케이션의 모든 요청을 1차적으로 받는 서블릿 정의
public class DispatcherServlet extends HttpServlet{
	/*
	 * 컨트롤러의 업무 처리 과정
	 * 1) 요청을 받음
	 * 2) 요청을 분석함 (어떤 요청을 원하는지 분석. 하위 컨트롤러를 선택하기 위함)
	 * 3) 알맞은 로직 객체에게 일을 시킴 
	 * (만약 직접 하게 되면, 그 순간 컨트롤러는 모델이 되어버리기 때문에 추후 다른 프로젝트에서 재사용할 수 없음..)
	 * 4) 업무 수행 후, view로 가져갈 것이 있다면 결과를 세션 or 요청 객체에 저장함
	 * DML-view로 가져갈 것 없음 / select=view로 가져갈 것 있음(ex. DTO, List 등)
	 * 5) 알맞은 view를 보여줌
	 */
	
	FileReader reader;
	FileInputStream fis;
	JSONParser jsonParser;
	JSONObject obj;
	
	//서블릿이 인스턴스화되고 나서, 초기화 과정을 수행할 때 호출되는 메서드
	@Override
	public void init(ServletConfig config) throws ServletException {
		//요청을 받기 전에, json파일에 Stream을 생성하여 해석(파싱)
		//fis=new FileInputStream("파일경로");
		jsonParser=new JSONParser();
		
		URL url=this.getClass().getResource("/org/sp/mvcapp/controller/mapper.js");
		
		try {
			reader=new FileReader(new File(url.toURI()));
			
			//문자기반 입력스트림
			obj=(JSONObject)jsonParser.parse(reader);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//GET, POST
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1단계) 요청을 받음
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out=response.getWriter();
		
	
		//2단계) 요청을 분석함
		//클라이언트가 요청 시 사용한 URI 자체가 곧, 원하는 것이 무엇인지에 대한 의미가 담겨져 있으므로
		//URI를 이용하여 클라이언트의 요청을 분석
		String uri=request.getRequestURI();
		
		/* 재사용성.효율설이 낮은 코드
		if(uri.equals("/movie.do")) { //클라이언트의 요청은 영화에 대한 조언을 원함
			out.print("영화에 대한 판단을 요청");
			//영화 전담 컨트롤러에게 요청을 전달
			MovieController controller=new MovieController();
			controller.doRequest(request, response);
		}else if(uri.equals("/blood.do")) { //클라이언트의 요청은 혈액형에 대한 조언을 원함
			out.print("혈액형에 대한 판단을 요청");
			//혈액형 전담 컨트롤러에게 요청을 전달
		}
		*/
		
		//요청이 /movie.do라면 무비컨트롤러를 메모리에 올리고, doRequest() 메서드 호출
		JSONObject controllerObj=(JSONObject)obj.get("controller");
		
		//이 요청에 대해 동작할 하위 컨트롤러의 경로를 얻어옴
		//단, 실제 객체는 아님, 따라서 인스턴스 생성 불가
		String controllerName=(String)controllerObj.get(uri);
		System.out.println("이 요청에 동작할 클래스 경로는 "+controllerName);
		
	}
	
	//서블릿의 인스턴스가 소멸되기 직전에 호출
	@Override
	public void destroy() {
		//만일 닫히지 않은 스트림이 있다면, 여기서 닫기
		if(reader!=null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
}
