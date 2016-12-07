package FE;

/**
 * Interface definition: FEBookingInt.
 * 
 * @author OpenORB Compiler
 */
public interface FEBookingIntOperations
{
    /**
     * Operation bookFlight
     */
    public String bookFlight(String firstName, String lastName, String address, String phone, String destination, String date, String classFlight);

    /**
     * Operation getBookedFlightCount
     */
    public String getBookedFlightCount(String recordType);

    /**
     * Operation editFlightRecord
     */
    public String editFlightRecord(String recordID, String fieldName, String newValue);

    /**
     * Operation transferReservation
     */
    public String transferReservation(String passengerID, String currentCity, String otherCity);

}
