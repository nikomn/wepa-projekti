package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {

    private String username;
    private String password;
    
    
    @OneToMany(mappedBy = "account")
    private List<Connection> connections = new ArrayList<>();
    
    @OneToMany(mappedBy = "owner")
    private List<FileObject> files = new ArrayList<>();
    
    @OneToMany(mappedBy = "posessor")
    private List<Skill> skills = new ArrayList<>();
    
    //@OneToOne(mappedBy = "owner")
    //private FileObject profilepic;
     
}
