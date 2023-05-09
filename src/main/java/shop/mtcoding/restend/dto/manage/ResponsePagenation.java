package shop.mtcoding.restend.dto.manage;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.mtcoding.restend.dto.user.UserResponse;

@Getter
public class ResponsePagenation<T,S> {

    private Integer status; // 에러시에 의미 있음.
    private String msg; // 에러시에 의미 있음. ex) badRequest
    private T data; // 에러시에는 구체적인 에러 내용 ex) username이 입력되지 않았습니다

    private S size;


    public ResponsePagenation(T data,S size){
        this.status = HttpStatus.OK.value();
        this.msg = "성공";
        this.data = data; // 응답할 데이터 바디
        this.size = size;
    }

}
