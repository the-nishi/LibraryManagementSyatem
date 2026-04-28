package iublibrary;

import java.time.LocalDate;

public class LibraryMember {
    private final int id;
    private final String name;
    private final String contactNumber;
    private final String department;
    private final String gender;
    private final LocalDate joiningDate;

    public LibraryMember(int id, String name, String contactNumber, String department, String gender, LocalDate joiningDate) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.department = department;
        this.gender = gender;
        this.joiningDate = joiningDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }
}