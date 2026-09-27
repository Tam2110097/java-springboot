package vn.tamnguyen.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String key; // field name, exp: firstName, lastName, email, id,...
    private String operation; // operation, exp: :, >, <,
    private Object value;
}
