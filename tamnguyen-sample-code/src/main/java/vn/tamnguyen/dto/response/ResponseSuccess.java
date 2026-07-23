package vn.tamnguyen.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class ResponseSuccess extends ResponseEntity<ResponseSuccess.Payload> {

    /*
    mô tả dữ liệu trả về cho các API PUT, PATCH, DELETE
    vì 3 method này không cần trả về dữ liệu gì, sau khi thực hiện xong chỉ cần trả về message
    */
    /*
    có thể truyền status vào thế chỗ cho HttpStatus.OK, nhưng sẽ gặp vấn đề ở method delete vì sẽ không hiển thị
    thông tin trả về ở trong body nên HttpStatus.OK truyền vào để giải quyết bài toán đó
    */
    public ResponseSuccess(HttpStatusCode status, String message) {
        super(new Payload(status.value(), message), HttpStatus.OK);
    }

    //GET, POST
    public ResponseSuccess(HttpStatusCode status, String message, Object data) {
        super(new Payload(status.value(), message, data), HttpStatus.OK);
    }

    public static class Payload {
        private final int status;
        private final String message;
        private Object data;

        public Payload(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public Payload(int status, String message, Object data) {
            this.message = message;
            this.status = status;
            this.data = data;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}
