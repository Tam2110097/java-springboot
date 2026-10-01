package vn.tamnguyen.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.tamnguyen.model.User;

import java.util.ArrayList;
import java.util.List;

import static vn.tamnguyen.repository.specification.SearchOperation.ZERO_OR_MORE_REGEX;

public class UserSpecificationBuilder {

    public final List<SpecSearchCriteria> param;

    public UserSpecificationBuilder() {
        this.param = new ArrayList<>();
    }

    public UserSpecificationBuilder with (String key, String operation, Object value, String prefix, String suffix) {
        return with (null, key, operation, value, prefix, suffix);
    }

    public UserSpecificationBuilder with (String orPredicate, String key, String operation, Object value, String prefix, String suffix) {
        SearchOperation oper = SearchOperation.getSimpleOperation(operation.charAt(0));
        if(oper == SearchOperation.EQUALITY) {
            boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
            boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);

            if(startWithAsterisk && endWithAsterisk) {
                oper = SearchOperation.CONTAINS;
            } else if(startWithAsterisk) {
                oper = SearchOperation.ENDS_WITH;
            } else if(endWithAsterisk) {
                oper = SearchOperation.STARTS_WITH;
            }
        }

        param.add(new SpecSearchCriteria(orPredicate, key, oper, value));
        return this;
    }

    public Specification<User> build() {
        if(param.isEmpty()) return null;

        Specification<User> specification = new UserSpecification(param.get(0));

        for(int i = 1; i < param.size(); i++) {
            specification = param.get(i).getOrPredicate()
                    ? Specification.where(specification).or(new UserSpecification(param.get(i)))
                    : Specification.where(specification).and(new UserSpecification(param.get(i)));

        }

        return specification;
    }
}
