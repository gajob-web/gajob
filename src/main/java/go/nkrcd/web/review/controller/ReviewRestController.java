package go.nkrcd.web.review.controller;

import go.nkrcd.web.main.model.RestEntity;
import go.nkrcd.web.main.model.User;
import go.nkrcd.web.main.service.UserService;
import go.nkrcd.web.review.model.AddReview;
import go.nkrcd.web.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
public class ReviewRestController {

    @Autowired
    UserService userService;

    @Autowired
    ReviewService reviewService;

    /*
     * 마이페이지 > 후기 작성하기 > 후기 등록하기
     */
    @RequestMapping(value = "", method = {RequestMethod.POST})
    public ResponseEntity save(AddReview addReview, Authentication authentication) {
        User user = userService.findUserByOauthId(authentication.getName());
        try {
            reviewService.save(addReview, user);
        } catch (RuntimeException e) {
            return new ResponseEntity(RestEntity.res(HttpStatus.INTERNAL_SERVER_ERROR, "후기가 등록에 실패하였습니다.", null), HttpStatus.OK);
        }
        return new ResponseEntity(RestEntity.res(HttpStatus.OK, "후기가 등록되었습니다.", null), HttpStatus.OK);
    }

}