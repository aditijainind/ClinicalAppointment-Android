package aditij.assignment4.data;

/**
 * Created by aditij on 2/23/2015.
 */
public class Physician {
    private long physicianId;
    private String name;
    private String specialization;

    public long getPhysicianId()
    {
        return this.physicianId;
    }

    public void setPhysicianId(long _physicianId)
    {
        this.physicianId = _physicianId;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String _name)
    {
        this.name = _name;
    }

    public String getSpecialization()
    {
        return this.specialization;
    }

    public void setSpecialization(String _specialization)
    {
        this.specialization= _specialization;
    }
}
