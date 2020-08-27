package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill extends AbstractPersistable<Long> {

    private String name;
    private Integer likes;

    @ManyToOne
    private Account posessor;

    //@Override
//    public int compareTo(Skill skill) {
//        if (this.likes == skill.getLikes()) {
//            return 0;
//        } else if (this.likes > skill.getLikes()) {
//            return 1;
//        } else {
//            return -1;
//        }
//    }

}
