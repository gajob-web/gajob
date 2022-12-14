package go.nkrcd.web.review.controller;

import go.nkrcd.web.main.model.Code;
import go.nkrcd.web.main.model.RestEntity;
import go.nkrcd.web.main.model.User;
import go.nkrcd.web.main.service.CodeService;
import go.nkrcd.web.main.service.CompanyService;
import go.nkrcd.web.main.service.UserService;
import go.nkrcd.web.review.model.AddReview;
import go.nkrcd.web.review.model.Level;
import go.nkrcd.web.review.repository.ReviewRepository;
import go.nkrcd.web.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/review")
public class ReviewController {
    private final CodeService codeService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final CompanyService companyService;

    @Autowired
    public ReviewController(CodeService codeService, UserService userService, ReviewService reviewService, CompanyService companyService) {
        this.codeService = codeService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.companyService = companyService;
    }

    /*
     * 후기 작성하기
     */
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String review(Model model) {
        List<Code> codes = codeService.findCodeList();
        AddReview addReview = new AddReview();
        addReview.setLevel(Level.valueOf("MID"));

        model.addAttribute("addReview", addReview);
        model.addAttribute("codes", codes);

        return "review/add";
    }

    /*
     * 후기 작성하기 > 후기 등록하기
     */
    @ResponseBody
    @RequestMapping(value = "", method = {RequestMethod.POST})
    public ResponseEntity save(AddReview addReview, Authentication authentication) {
        User user = userService.findByOauthIdAndDelYn(authentication.getName());
        try {
            if(user.getOauthId() == null) throw new RuntimeException("사용자 확인 불가");
            reviewService.save(addReview, user);
        } catch (RuntimeException e) {
            return new ResponseEntity(RestEntity.res(HttpStatus.INTERNAL_SERVER_ERROR, "후기가 등록에 실패하였습니다.", null), HttpStatus.OK);
        }
        return new ResponseEntity(RestEntity.res(HttpStatus.OK, "후기가 등록되었습니다.", null), HttpStatus.OK);
    }

    /*
     * 후기 보기
     */
    @RequestMapping(value = "/view", method = {RequestMethod.GET})
    public String view(Model model, @RequestParam(name = "company") String cId) {
        model.addAttribute("company", companyService.findCompanyView(cId));
        model.addAttribute("rates", reviewService.findLevelRate(cId));
        model.addAttribute("top3", reviewService.findExperienceTop3(cId));
        model.addAttribute("reviews", reviewService.findReviews(cId));

        return "review/view";
    }

    /**
     * 후기 삭제
     * @param rid
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "", method = {RequestMethod.PUT})
    public ResponseEntity del(String rid) {
        if(reviewService.del(rid) > -1) {
            return new ResponseEntity(RestEntity.res(HttpStatus.OK, "후기가 삭제되었습니다.", null), HttpStatus.OK);
        } else {
            return new ResponseEntity(RestEntity.res(HttpStatus.INTERNAL_SERVER_ERROR, "후기가 삭제 실패하였습니다.", null), HttpStatus.OK);
        }
    }
}
