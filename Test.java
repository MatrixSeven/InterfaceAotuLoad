package autoload;

import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		
		for(Class<Anni> l:InterfaceHelp.getDataClass(Anni.class)){
			System.out.println(l.getName());
		}
		System.out.println("==================================");
		for(Class<Anni> l:InterfaceHelp.getDataClass("autoload",Anni.class)){
			System.out.println(l.getName());
		}
		List<Anni> annis=new ArrayList<>();
		System.err.println("===================================");

		InterfaceHelp.getDataClass("autoload",Anni.class).forEach(e->annis.add(InterfaceHelp.getInstance(e)));
		annis.forEach(e->e.PrintName());
		
		
	}

}
