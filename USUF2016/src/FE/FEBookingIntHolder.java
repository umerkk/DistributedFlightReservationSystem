package FE;

/**
 * Holder class for : FEBookingInt
 * 
 * @author OpenORB Compiler
 */
final public class FEBookingIntHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal FEBookingInt value
     */
    public FE.FEBookingInt value;

    /**
     * Default constructor
     */
    public FEBookingIntHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public FEBookingIntHolder(FE.FEBookingInt initial)
    {
        value = initial;
    }

    /**
     * Read FEBookingInt from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = FEBookingIntHelper.read(istream);
    }

    /**
     * Write FEBookingInt into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        FEBookingIntHelper.write(ostream,value);
    }

    /**
     * Return the FEBookingInt TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return FEBookingIntHelper.type();
    }

}
