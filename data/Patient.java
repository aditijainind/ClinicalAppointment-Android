package aditij.assignment4.data;

/**
 * Created by aditij on 2/23/2015.
 */
public class Patient {
    private long patientId;
    private String name;
    private String address;

    public long getPatientId()
    {
        return this.patientId;
    }

    public void setPatientId(long _patientId)
    {
        this.patientId = _patientId;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String _name)
    {
        this.name = _name;
    }

    public String getAddress()
    {
        return this.address;
    }

    public void setAddress(String _address)
    {
        this.address= _address;
    }
}
