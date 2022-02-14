package ai.hyperlearning.ontopop.model.git;

import java.io.Serializable;

/**
 * Git committer model
 * Reference: https://docs.github.com/en/rest/reference/repos#create-or-update-file-contents
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GitCommitter implements Serializable {

    private static final long serialVersionUID = 3481308833262012705L;
    private String name;
    private String email;
    
    public GitCommitter() {
        
    }

    public GitCommitter(String name, String email) {
        super();
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GitCommitter ["
                + "name=" + name + ", "
                + "email=" + email + 
                "]";
    }

}
