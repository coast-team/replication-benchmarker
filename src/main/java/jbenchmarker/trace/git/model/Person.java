/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.trace.git.model;

import java.util.Date;
import org.eclipse.jgit.lib.PersonIdent;

/**
 * Bean corresponding to PersonIdent of JGit
 * @author urso
 */
public class Person {
    private String name;
    private String email;
    private String timeZone;
    private Date when;
    
    public Person() {
    }
    
    public Person(PersonIdent p) {
        name = p.getName();
        email = p.getEmailAddress();
        timeZone = p.getTimeZone().toString();
        when = p.getWhen();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }
}
