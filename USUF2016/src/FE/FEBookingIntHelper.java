package FE;

/** 
 * Helper class for : FEBookingInt
 *  
 * @author OpenORB Compiler
 */ 
public class FEBookingIntHelper
{
    /**
     * Insert FEBookingInt into an any
     * @param a an any
     * @param t FEBookingInt value
     */
    public static void insert(org.omg.CORBA.Any a, FE.FEBookingInt t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract FEBookingInt from an any
     *
     * @param a an any
     * @return the extracted FEBookingInt value
     */
    public static FE.FEBookingInt extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return FE.FEBookingIntHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the FEBookingInt TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "FEBookingInt" );
        }
        return _tc;
    }

    /**
     * Return the FEBookingInt IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:FE/FEBookingInt:1.0";

    /**
     * Read FEBookingInt from a marshalled stream
     * @param istream the input stream
     * @return the readed FEBookingInt value
     */
    public static FE.FEBookingInt read(org.omg.CORBA.portable.InputStream istream)
    {
        return(FE.FEBookingInt)istream.read_Object(FE._FEBookingIntStub.class);
    }

    /**
     * Write FEBookingInt into a marshalled stream
     * @param ostream the output stream
     * @param value FEBookingInt value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, FE.FEBookingInt value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to FEBookingInt
     * @param obj the CORBA Object
     * @return FEBookingInt Object
     */
    public static FEBookingInt narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof FEBookingInt)
            return (FEBookingInt)obj;

        if (obj._is_a(id()))
        {
            _FEBookingIntStub stub = new _FEBookingIntStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to FEBookingInt
     * @param obj the CORBA Object
     * @return FEBookingInt Object
     */
    public static FEBookingInt unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof FEBookingInt)
            return (FEBookingInt)obj;

        _FEBookingIntStub stub = new _FEBookingIntStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
