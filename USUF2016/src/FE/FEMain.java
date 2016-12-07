package FE;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class FEMain {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Front End has started.");
		
		try {
			//CORBA configuration
			ORB orb = ORB.init(args, null);
			POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootPOA.the_POAManager().activate();
			FEBookingImpl bookingObject = new FEBookingImpl();
			bookingObject.setORB(orb);
			org.omg.CORBA.Object ref = rootPOA.servant_to_reference(bookingObject);
			FEBookingInt href = FEBookingIntHelper.narrow(ref);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			NameComponent path[] = ncRef.to_name("FRONTEND");
			ncRef.rebind(path, href);
			
			while(true)
			{
				orb.run();
			}
			
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CORBA.ORBPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServantNotActive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongPolicy e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AdapterInactive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
