package com.adnstyle.myboard.controller;

import com.adnstyle.myboard.model.domain.JyAttach;
import com.adnstyle.myboard.model.domain.JyBoard;
import com.adnstyle.myboard.model.domain.JyReply;
import com.adnstyle.myboard.model.domain.PageHandle;
import com.adnstyle.myboard.model.service.JyAttachService;
import com.adnstyle.myboard.model.service.JyBoardService;
import com.adnstyle.myboard.model.service.JyReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")//로그인한 사람들만 허용할것!
public class JyBoardController {

    private final JyBoardService jyBoardService;

    private final JyAttachService jyAttachService;

    private final JyReplyService jyReplyService;


    /**
     * 사이트메인화면(로그인후 뜰 화면)
     */
    @GetMapping("/jyHome")
    public String jyHome() {
        return "jyHome";
    }

    /**
     * 게시글 리스트(자유,답변)+페이징처리+검색처리(자유게시판)
     */
    @GetMapping("/boardList")
    public String qnaBoardList(Model model, @RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "type", defaultValue = "A") String type,
                               @RequestParam(value = "search", defaultValue = "") String search, @RequestParam("boardType") String boardType) {
        Map searchMap = new HashMap();
        searchMap.put("type", type);
        searchMap.put("search", search);
        searchMap.put("boardType", boardType);
        int totalCnt = jyBoardService.countAll(searchMap);

        PageHandle ph = new PageHandle(totalCnt, page);

        searchMap.put("offset", ((page - 1) * ph.getPageSize()));
        searchMap.put("pageSize", ph.getPageSize());

        List<JyBoard> myBoardList = jyBoardService.selectList(searchMap);//게시글리스트 조회용

        model.addAttribute("myBoardList", myBoardList);
        model.addAttribute("ph", ph);
        model.addAttribute("type", type);
        model.addAttribute("search", search);
        model.addAttribute("boardType", boardType);
        if (boardType.equals("QnA_Board")) {
            return "qnaBoardList";
        } else if (boardType.equals("Free_Board")) {
            return "freeBoardList";
        }
        return null;
    }

    /**
     * 게시글 상세조회
     */
    @GetMapping("/boardContent")
    public String myBoardContent(Model model, long id, @RequestParam("page") int page, @RequestParam("type") String type, @RequestParam("search") String search) {
        List<JyBoard> myContent = jyBoardService.selectContent(id);//게시글 번호로 내용 불러오기
        List<JyAttach> attachList = jyAttachService.attachList(id);//첨부파일리스트 조회하기

        model.addAttribute("myContent", myContent);//게시글내용
        model.addAttribute("attachList", attachList);//첨부파일

        model.addAttribute("page", page);//페이지
        model.addAttribute("type", type);//검색타입
        model.addAttribute("search", search);//검색내용
        return "freeBoardContent";

    }


    /**
     * 게시글 삭제(상태값 변경)
     */
    @GetMapping("/deleteContent")
    public String myBoardContentDelete(@RequestParam long id, @RequestParam String boardType, RedirectAttributes rttr) {
        jyBoardService.deleteContent(id);
        rttr.addAttribute("id", id);
        rttr.addAttribute("boardType", boardType);
        return "redirect:/user/boardList";
    }

    /**
     * 자유게시글 작성폼으로 이동
     */
    @GetMapping("/freeBoardForm")
    public String freeBoardForm() {
        return "freeBoardForm";
    }

    /**
     * 문의글 작성폼으로 이동
     */
    @GetMapping("/questionsForm")
    public String WriteForm() {
        return "questionForm";
    }

    /**
     * 게시글(질문글,자유게시판) 등록 + 첨부파일등록
     */
    @PostMapping("/insertContent")
    public String myBoardInsertContent(MultipartFile[] uploadFile, JyBoard board, RedirectAttributes rttr) {
        jyBoardService.insertContent(uploadFile, board);
        String boardType = board.getBoardType();
        rttr.addAttribute("boardType", boardType);

        return "redirect:/user/boardList";
    }


    /**
     * 첨부파일 다운로드
     */
    @GetMapping(value = "/downloadFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFile(String uploadPath, String fileName) {
        
        Resource resource = new FileSystemResource(uploadPath + "/" + fileName);// mac은 "/" windows는 "\"
        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Disposition", "attachment; " + "fileName=" +
                    new String(resourceName.getBytes("UTF-8"),
                            "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }


    /**
     * 게시글 수정하기
     */
    @PostMapping("/updateContent")
    public String myBoardUpdateContent(JyBoard board, MultipartFile[]
            uploadFile, @RequestParam(value = "attBno", required = false) List<Long> attList, @RequestParam("boardType") String boardType,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "type", defaultValue = "A") String type,
                                       @RequestParam(value = "search", defaultValue = "") String search, RedirectAttributes rttr) {//HttpServletRequest request
        List attFileList = new ArrayList();

        if (attList != null) {
            for (Long attBno : attList) {
                attFileList.add(attBno);
            }
            //jyAttachService.deleteOnlyAttach(attFileList);//첨부파일만 완전삭제
            jyAttachService.delAttachYn(attFileList);//첨부파일 상태값 변경
        }
        jyBoardService.updateContent(board, uploadFile);//게시글수정

        if (boardType.equals("QnA_Board")) {
            rttr.addAttribute("page", page);
            rttr.addAttribute("boardType", boardType);
            return "redirect:/user/boardList";
        } else if (boardType.equals("Free_Board")) {
            rttr.addAttribute("page", page);
            rttr.addAttribute("search", search);
            rttr.addAttribute("type", type);
            rttr.addAttribute("boardType", boardType);
            return "redirect:/user/boardList";
        }
        return null;
    }

    /**
     * 답변달기 폼으로 이동
     */
    @GetMapping("/answerForm")
    public String writeForm(@RequestParam("id") Long id, @RequestParam("boardType") String boardType, Model model) {

        model.addAttribute("id", id);//그룹아이디가 될것
        model.addAttribute("boardType", boardType);//그룹아이디가 될것

        return "answerForm";
    }

    /**
     * 답변등록
     */
    @PostMapping("/insertAnswer")
    public String insertAnswer(MultipartFile[] uploadFile, JyBoard board, RedirectAttributes rttr) {
        jyBoardService.insertContent(uploadFile, board);
        String boardType = board.getBoardType();
        rttr.addAttribute("boardType", boardType);
        return "redirect:/user/boardList";
    }

    /**
     * 답변글 상세조회
     */
    @GetMapping("/answerContent")
    public String answerContent(Model model, long id, @RequestParam("page") int page) {
        log.debug("답 상세조회 컨트롤러=========================================");
        List<JyBoard> myAnswerContent = jyBoardService.selectContent(id);//게시글 번호로 내용 불러오기
        List<JyAttach> attachList = jyAttachService.attachList(id);
        model.addAttribute("myAnswerContent", myAnswerContent);//게시글내용
        model.addAttribute("attachList", attachList);//첨부파일
        model.addAttribute("page", page);//페이지

        return "answerContent";
    }

    /**
     * 문의글 상세조회
     */
    @GetMapping("/qnaContent")
    public String qnaContent(Model model, long id, @RequestParam("page") int page) {
        List<JyBoard> myQnAContent = jyBoardService.selectContent(id);//게시글 번호로 내용 불러오기
        List<JyAttach> attachList = jyAttachService.attachList(id);
        model.addAttribute("myQnAContent", myQnAContent);//게시글내용
        model.addAttribute("attachList", attachList);//첨부파일
        model.addAttribute("page", page);//페이지

        return "questionContent";
    }

    /**
     * 답변삭제
     */
    @GetMapping("/deleteAnswer")
    public String deleteAnswer(Long id, @RequestParam("boardType") String boardType, RedirectAttributes rttr) {
        jyBoardService.deleteAnswer(id);
        rttr.addAttribute("boardType", boardType);
        return "redirect:/user/boardList";
    }

    /**
     * 댓글작성
     */

    @PostMapping("/insertReply")
    @ResponseBody
    public Map<String, String> replySub(@RequestBody JyReply jyReply) {
        Map<String, String> map = new HashMap<>();
        jyReplyService.insertReply(jyReply);
        map.put("result", "success");

        return map;
    }

    /**
     * 대댓글작성
     */

    @PostMapping("/insertChildReply")
    @ResponseBody
    public Map insertChildReply(@RequestBody JyReply childReply) {
        Map<String, String> map = new HashMap<>();
        jyReplyService.insertChildReply(childReply);
        map.put("result", "success");

        return map;
    }

    /**
     * 댓글삭제
     */

    @PostMapping("/deleteReply")
    @ResponseBody
    public Map deleteReply(@RequestParam(value = "delReBno") Long delReBno) {
        Map<String, String> map = new HashMap<>();
        jyReplyService.deleteReply(delReBno);
        map.put("result", "success");

        return map;

    }

    /**
     * 댓글조회
     */
    @GetMapping("/selectReplyList")
    @ResponseBody
    public Map<String, Object> selectReplyList(@RequestParam(value = "boardBno") Long boardBno,
                                               @RequestParam(value = "page", defaultValue = "1") int page) {

        return jyReplyService.selectReplyList(boardBno, page);

    }
}


