package aditij.assignment4.data;

/**
 * Created by aditij on 2/23/2015.
 */
public class Appointment {
    private long mappingId;
    private long patientId;
    private long physicianId;
    private String patientName;
    private String physicianName;
    private String diagnosis;
    private String visitTime;
    private String physicianDepartment;
    public long getMappingId()
    {
        return this.mappingId;
    }

    public void setMappingId(long _mappingId)
    {
        this.mappingId = _mappingId;
    }

    public long getPatientId()
    {
        return this.patientId;
    }

    public void setPatientId(long _patientId)
    {
        this.patientId = _patientId;
    }

    public long getPhysicianId()
    {
        return this.physicianId;
    }

    public void setPhysicianId(long _physicianId)
    {
        this.physicianId = _physicianId;
    }

    public String getPatientName()
    {
        return this.patientName;
    }

    public void setPatientName(String _patientName)
    {
        this.patientName = _patientName;
    }

    public String getPhysicianName()
    {
        return this.physicianName;
    }

    public void setPhysicianName(String _physicianName)
    {
        this.physicianName = _physicianName;
    }

    public String getDiagnosis()
    {
        return this.diagnosis==null?this.diagnosis:"";
    }

    public void setDiagnosis(String _diagnosis)
    {
        this.diagnosis = _diagnosis;
    }
    public String getVisitTime()
    {
        return this.visitTime;
    }

    public void setVisitTime(String _visitTime)
    {
        this.visitTime = _visitTime;
    }

    public String getPhysicianDepartment()
    {
        return this.physicianDepartment;
    }

    public void setPhysicianDepartment(String _physicianDepartment)
    {
        this.physicianDepartment = _physicianDepartment;
    }
}
