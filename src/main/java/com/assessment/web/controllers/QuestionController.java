package com.assessment.web.controllers;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.assessment.Exceptions.AssessmentGenericException;
import com.assessment.common.CommonUtil;
import com.assessment.common.ExcelReader;
import com.assessment.data.Company;
import com.assessment.data.DifficultyLevel;
import com.assessment.data.ProgrammingLanguage;
import com.assessment.data.Question;
import com.assessment.data.QuestionType;
import com.assessment.data.User;
import com.assessment.services.CompanyService;
import com.assessment.services.QuestionService;
import com.assessment.services.UserService;

@Controller
public class QuestionController {
	@Autowired
	private UserService userService;
	@Autowired
	private QuestionService questionService;
	@Autowired
	private CompanyService companyService;
	
	Logger logger =LoggerFactory.getLogger(QuestionController.class);
	
	@RequestMapping(value = "/goback", method = RequestMethod.GET)
	public ModelAndView goback(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
//		List<Question> questions = questionService.findQuestions(user.getCompanyId());
//		mav.addObject("qs", questions);
		return listQuestions(null, response, request, mav.getModelMap());
		//return mav;
	}

	@RequestMapping(value = "/addQuestion", method = RequestMethod.GET)
	public ModelAndView addQuestion(@RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam(name= "qid", required = false)Long qid,HttpServletRequest request,HttpServletResponse response)throws Exception {
		ModelAndView mav = new ModelAndView("add_question");
		
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		if(qid == null) {
			Question question = new Question();
			mav.addObject("question", question);
			mav.addObject("question_label", "Add new Question");
		}
		else {
			Question question = questionService.findById(qid);
			question.setType(question.getQuestionType() != null? question.getQuestionType().getType():QuestionType.MCQ.getType());
			question.setLang(question.getLanguage() != null?question.getLanguage().getLanguage():ProgrammingLanguage.JAVA.getLanguage());
			
			question.setUpFromInUpdateMode();
			mav.addObject("question", question);
			mav.addObject("editQMode", "true");
			mav.addObject("question_label", "Update this Question");
		}
		
		
		Page<Question> questions = questionService.findQuestionsByPage(user.getCompanyId(), pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "addQuestion", null);
		return mav;
	}
	
	@RequestMapping(value = "/removeQuestionFromList", method = RequestMethod.GET)
	public ModelAndView removeQuestionFromList(@RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam(name= "qid", required = false) Long qid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("question_list");
		if(qid != null) {
			Boolean canDelete = questionService.canDeleteQuestion(qid);
				if(canDelete) {
					questionService.removeQuestion(qid);
					mav.addObject("message", "Question successfully deleted");// later put it as label
					mav.addObject("msgtype", "success");
				}
				else {
					mav.addObject("message", "This Question is associated with one or more Tests. Can not delete this Q");// later put it as label
					mav.addObject("msgtype", "failure");
				}
		}
		else {
			mav.addObject("message", "Nothing to remove");// later put it as label
			mav.addObject("msgtype", "failure");
		}
		
		
		Question question = new Question();
		mav.addObject("question", question);
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.findQuestionsByPage(user.getCompanyId(), pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "question_list", null);
		return mav;
	}
	
	@RequestMapping(value = "/removeQuestion", method = RequestMethod.GET)
	public ModelAndView removeQuestion(@RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam(name= "qid", required = false) Long qid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("add_question");
		if(qid != null) {
			Boolean canDelete = questionService.canDeleteQuestion(qid);
				if(canDelete) {
					questionService.removeQuestion(qid);
					mav.addObject("message", "Question successfully deleted");// later put it as label
					mav.addObject("msgtype", "success");
				}
				else {
					mav.addObject("message", "This Question is associated with one or more Tests. Can not delete this Q");// later put it as label
					mav.addObject("msgtype", "failure");
				}
		}
		else {
			mav.addObject("message", "Nothing to remove");// later put it as label
			mav.addObject("msgtype", "failure");
		}
		
		
		Question question = new Question();
		mav.addObject("question", question);
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.findQuestionsByPage(user.getCompanyId(), pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "addQuestion", null);
		return mav;
	}

	@RequestMapping(value = "/saveQuestion", method = RequestMethod.POST)
	public ModelAndView saveQuestion(HttpServletRequest request, HttpServletResponse response,@ModelAttribute("question") Question question) throws Exception {
		ModelAndView mav = null;
		User user = (User) request.getSession().getAttribute("user");
		List<Question> questions = new ArrayList<Question>();
		try {
			question.setup();
		} catch (AssessmentGenericException e) {
			// TODO Auto-generated catch block
			questions = questionService.findQuestions(user.getCompanyId());
			mav = new ModelAndView("add_question");
			mav.addObject("question", question);
			mav.addObject("message", "Select atleast 1 Correct answer");// later put it as label
			mav.addObject("msgtype", "failure");
			mav.addObject("qs", questions);
			mav.addObject("levels", DifficultyLevel.values());
			return mav;
		}
		question.setCompanyId(user.getCompanyId());
		question.setCompanyName(user.getCompanyName());
			if((!question.getInputCode().contains("<br />")) && question.getInputCode() != null){
				question.setInputCode(question.getInputCode().replaceAll("\\r\\n|\\r|\\n", "<br />"));
			}
			
			if( (!question.getInstructionsIfAny().contains("<br />")) && question.getInstructionsIfAny() != null){
				question.setInstructionsIfAny(question.getInstructionsIfAny().replaceAll("\n", "<br />"));
			}
		
			if(question.getId() != null) {
				questionService.updateQuestion(question);
			}
			else {
				questionService.createQuestion(question);
			}
		
//		questions = questionService.findQuestions(user.getCompanyId());
//		mav = new ModelAndView("question_list");
//		mav.addObject("qs", questions);
//		return mav;
		mav = new ModelAndView("add_question");
		mav.addObject("message", "Question Save Success");// later put it as label
		mav.addObject("msgtype", "Success");
		//return listQuestions(null, response, request, mav.getModelMap());
		question = new Question();
		question.setType(question.getQuestionType() != null? question.getQuestionType().getType():QuestionType.MCQ.getType());
		question.setLang(question.getLanguage() != null?question.getLanguage().getLanguage():ProgrammingLanguage.JAVA.getLanguage());
		mav.addObject("question", question);
		mav.addObject("question_label", "Add new Question");
		Page<Question> questions2 = questionService.findQuestionsByPage(user.getCompanyId(), 0);
		mav.addObject("qs", questions2.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		CommonUtil.setCommonAttributesOfPagination(questions2, mav.getModelMap(), 0, "addQuestion", null);
		return mav;
	}

	// @RequestMapping(value = "/searchQByQualifier1/{qualifier1}", method =
	// RequestMethod.GET)
	// public ModelAndView searchQByQualifier1(@PathVariable String qualifier1,
	// HttpServletRequest request, HttpServletResponse response) {
	// ModelAndView mav = new ModelAndView("question_list");
	// User user = (User) request.getSession().getAttribute("user");
	// List<Question> questions =
	// questionService.findQuestionsByQualifier1(user.getCompanyId(), qualifier1);
	// mav.addObject("qs", questions);
	// mav.addObject("levels", DifficultyLevel.values());
	// return mav;
	// }

	@RequestMapping(value = "/searchQByQualifier1", method = RequestMethod.GET)
	public ModelAndView searchQByQualifier1(@RequestParam(name= "page", required = false) Integer pageNumber,@RequestParam String qualifier1, HttpServletRequest request,
			HttpServletResponse response) {
		String referer = request.getHeader("Referer");
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.findQuestionsByQualifier1AndPage(user.getCompanyId(), qualifier1, pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		Map<String, String> params = new HashMap<>();
		params.put("qualifier1", qualifier1);
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "searchQByQualifier1", params);
		return mav;
	}

	@RequestMapping(value = "/searchQByQualifier1And2", method = RequestMethod.GET)
	public ModelAndView searchQByQualifier1And2(@RequestParam(name= "page", required = false) Integer pageNumber,@RequestParam String qualifier1, @RequestParam String qualifier2,
			HttpServletRequest request, HttpServletResponse response) {
		String referer = request.getHeader("Referer");
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.findQuestionsByQualifier2AndPage(user.getCompanyId(), qualifier1,
				qualifier2, pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		Map<String, String> params = new HashMap<>();
		params.put("qualifier1", qualifier1);
		params.put("qualifier2", qualifier2);
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "searchQByQualifier1And2", params);
		return mav;
	}

	@RequestMapping(value = "/searchQByQualifier1And2And3", method = RequestMethod.GET)
	public ModelAndView searchQByQualifier1And2And3(@RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String qualifier1, @RequestParam String qualifier2,
			@RequestParam String qualifier3, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.findQuestionsByQualifier3AndPage(user.getCompanyId(), qualifier1,
				qualifier2, qualifier3, pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		Map<String, String> params = new HashMap<>();
		params.put("qualifier1", qualifier1);
		params.put("qualifier2", qualifier2);
		params.put("qualifier3", qualifier3);
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "searchQByQualifier1And2And3", params);
		return mav;
	}

	@RequestMapping(value = "/searchQByQualifier1And2And3And4", method = RequestMethod.GET)
	public ModelAndView searchQByQualifier1And2And3And4(@RequestParam(name= "page", required = false) Integer pageNumber,@RequestParam String qualifier1,
			@RequestParam String qualifier2, @RequestParam String qualifier3, @RequestParam String qualifier4,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.findQuestionsByQualifier4AndPage(user.getCompanyId(), qualifier1,
				qualifier2, qualifier3, qualifier4, pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		Map<String, String> params = new HashMap<>();
		params.put("qualifier1", qualifier1);
		params.put("qualifier2", qualifier2);
		params.put("qualifier3", qualifier3);
		params.put("qualifier4", qualifier4);
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "searchQByQualifier1And2And3And4", params);
		return mav;
	}

	@RequestMapping(value = "/searchQByQualifier1And2And3And4And5", method = RequestMethod.GET)
	public ModelAndView searchQByQualifier1And2And3And4And5(@RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String qualifier1,
			@RequestParam String qualifier2, @RequestParam String qualifier3, @RequestParam String qualifier4,
			@RequestParam String qualifier5, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions  = questionService.findQuestionsByQualifier5AndPage(user.getCompanyId(), qualifier1,
				qualifier2, qualifier3, qualifier4, qualifier5, pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		Map<String, String> params = new HashMap<>();
		params.put("qualifier1", qualifier1);
		params.put("qualifier2", qualifier2);
		params.put("qualifier3", qualifier3);
		params.put("qualifier4", qualifier4);
		params.put("qualifier5", qualifier5);
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "searchQByQualifier1And2And3And4And5", params);
		return mav;
	}

	@RequestMapping(value = "/searchQuestions", method = RequestMethod.GET)
	public ModelAndView searchQuestions(@RequestParam(name= "page", required = false) Integer pageNumber,@RequestParam String searchText, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions  = questionService.searchQuestions( user.getCompanyId(), searchText, pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		Map<String, String> params = new HashMap<>();
		params.put("searchText", searchText);
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "searchQuestions", params);
		return mav;
	}

	@RequestMapping(value = "/searchQuestions2", method = RequestMethod.GET)
	public ModelAndView searchQuestions2(@RequestParam(name= "page", required = false) Integer pageNumber, @RequestParam String searchText, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("add_question");
		Question question = new Question();
		mav.addObject("question", question);
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.searchQuestions(user.getCompanyId(), searchText, pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		CommonUtil.setCommonAttributesOfPagination(questions, mav.getModelMap(), pageNumber, "searchQuestions2", null);
		return mav;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void uploadQuestions(HttpServletResponse response, MultipartHttpServletRequest request) throws Exception {
		try {
			MultipartFile multipartFile = request.getFile("fileQuestions");
			Long size = multipartFile.getSize();
			String contentType = multipartFile.getContentType();
			InputStream stream = multipartFile.getInputStream();
			File file = new File("questions.xml");
			List<Question> questions = ExcelReader.parseExcelFileToBeans(stream, file);
			logger.info("in upload method qs size "+questions.size());
				if(questions.size() == 0) {
					throw new AssessmentGenericException("NO_DATA_IN_EXCEL");
				}
			String compId = questions.get(0).getCompanyId();
			System.out.println("comp id is "+compId);
			Company company = companyService.findByCompanyId(compId);
			System.out.println("Company got in uploadQuestions "+company.getId() +" "+company.getCompanyName());
			logger.info("Company got in uploadQuestions "+company.getId() +" "+company.getCompanyName());
			for (Question q : questions) {
				System.out.println(q.getQuestionText());
				q.setCompanyId(company.getCompanyId());
				q.setCompanyName(company.getCompanyName());
				questionService.createQuestion(q);
			}
			logger.info("upload qs in db complete");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("problem in uploading qs", e);
			throw new AssessmentGenericException("problem in uploading qs", e);
		}
	}
	
	@RequestMapping(value = "/question_list", method = RequestMethod.GET)
	public ModelAndView listQuestions(@RequestParam(name= "page", required = false) Integer pageNumber, HttpServletResponse response, HttpServletRequest request, ModelMap modelMap) throws Exception {
		ModelAndView mav = new ModelAndView("question_list");
		User user = (User) request.getSession().getAttribute("user");
		if(pageNumber == null) {
			pageNumber = 0;
		}
		Page<Question> questions = questionService.findQuestionsByPage(user.getCompanyId(), pageNumber);
		mav.addObject("qs", questions.getContent());
		mav.addObject("levels", DifficultyLevel.values());
		mav.addObject("types", QuestionType.values());
  		mav.addObject("languages", ProgrammingLanguage.values());
		CommonUtil.setCommonAttributesOfPagination(questions, modelMap, pageNumber, "question_list", null);
		return mav;
	}
}
