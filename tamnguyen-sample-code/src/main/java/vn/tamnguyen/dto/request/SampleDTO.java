package vn.tamnguyen.dto.request;

import lombok.*;

import java.io.Serializable;

/*
- @Builder có tác dụng: thay vì tạo constructor với các tham số và khi truyền vào ta không nhớ thứ tự hoặc cần 1 constructor
với các tham số khác ta cần phải viết lại 1 constructor mới thì với builder ta có thể dễ dàng khởi tạo 1 đối tượng mà
không cần truyền chính xác thứ tự tham số hay thêm hoặc bớt tham số 1 cách dễ dàng mà không cần viết thêm constructor

User user = User.builder()
        .firstName("Tam")
        .lastName("Nguyen")
        .email("tam@gmail.com") // Truyền trường nào cũng được, trường nào không truyền sẽ nhận giá trị mặc định (null/0)
        .age(25)
        .build();

- @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode

- @RequiredArgsConstructor khi 1 trường được định nghĩa là final thì annotation này sẽ tạo 1 constructor với các than số
là các trường được định nghĩa final đóng

- @EqualsAndHashCode(onlyExplicitlyIncluded = true) // mặc định là false
public class SampleDTO implements Serializable {
    private Integer id;
    @EqualsAndHashCode.Include
    private String name;
}

chỉ tính toán dựa trên các trường được đánh dấu là Include,
ngược lại nếu để onlyExplicitlyIncluded = false (mặc định) thì sẽ loại bỏ các trường được đánh dấu là Exclude

- @ToString(includeFieldNames = true) mặc định
public class SampleDTO implements Serializable {
    private Integer id;
    private String name;
}
includeFieldNames hiển thị hay không tên của trường đó vd: "id" + this.id
nếu để là false thì sẽ chỉ hiển thị giá trị vd: this.id

@ToString cũng có Include và Exclude tương tự như @EqualsAndHashCode

- @Slf4j hỗ trợ ghi log

- @NonNull kiểm tra các trường nếu null thì sẽ quăng ra exception

- @Getter(AccessLevel.PROTECTED) mặc định của lombok getter setter là public nhưng ta cũng có thể chỉ định truy xuất
*/
public class SampleDTO implements Serializable {
    private Integer id;
    private String name;
}
