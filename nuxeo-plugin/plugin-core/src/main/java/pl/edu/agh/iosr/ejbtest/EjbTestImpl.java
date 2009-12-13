package pl.edu.agh.iosr.ejbtest;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("ejbtest")
@Scope(ScopeType.APPLICATION)
@Stateless
public class EjbTestImpl implements EjbTest {

	public String getString() {
		return "udalo sie!";
	}

}
