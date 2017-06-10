package com.event.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Represents Event model. Note: Time is represent by the number of minutes in a day
 *
 * [Ref] https://spring.io/guides/gs/accessing-data-mongodb/
 */
@Document(collection = "event")
public class Event {

    @Id
    private long id;

    @Indexed(unique = true)
    private String title;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date end;

    private int startTime;
    private int endTime;

    private Contact contact;

    private Location location;

    private List<Long> users;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date updatedDate;

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getStartTime() {
        return startTime;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getFormattedStartTime() {
        return formatTime(startTime);
    }

    public void setStartTime(int startTime) {
        if (startTime < 0 || startTime > 1439) {
            throw new IllegalArgumentException("Start time must be from 0 to 1439");
        }
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getFormattedEndTime() {
        return formatTime(endTime);
    }

    public void setEndTime(int endTime) {
        if (endTime < 0 || endTime > 1439) {
            throw new IllegalArgumentException("Start time must be from 0 to 1439");
        }
        this.endTime = endTime;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    private String formatTime(int time) {
        int hours = (int) time/60;
        int minutes = time - hours * 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != event.id) return false;
        if (startTime != event.startTime) return false;
        if (endTime != event.endTime) return false;
        if (active != event.active) return false;
        if (title != null ? !title.equals(event.title) : event.title != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        if (start != null ? !start.equals(event.start) : event.start != null) return false;
        if (end != null ? !end.equals(event.end) : event.end != null) return false;
        if (contact != null ? !contact.equals(event.contact) : event.contact != null) return false;
        if (location != null ? !location.equals(event.location) : event.location != null) return false;
        if (users != null ? !users.equals(event.users) : event.users != null) return false;
        if (createdDate != null ? !createdDate.equals(event.createdDate) : event.createdDate != null) return false;
        return updatedDate != null ? updatedDate.equals(event.updatedDate) : event.updatedDate == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + startTime;
        result = 31 * result + endTime;
        result = 31 * result + (contact != null ? contact.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    public static class Contact {
        private String name;
        private String phone;
        private String emailAddress;

        // For serialization
        public Contact() {
        }

        public Contact(String name, String phone, String emailAddress) {
            this.name = name;
            this.phone = phone;
            this.emailAddress = emailAddress;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Contact contact = (Contact) o;

            if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
            if (phone != null ? !phone.equals(contact.phone) : contact.phone != null) return false;
            return emailAddress != null ? emailAddress.equals(contact.emailAddress) : contact.emailAddress == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (phone != null ? phone.hashCode() : 0);
            result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
            return result;
        }
    }

    public static class Location {
        private String address1;
        private String address2;
        private String city;
        private String state;
        private String zip;

        // For serialization
        public Location() {
        }

        public Location(String address1, String address2, String city, String state, String zip) {
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.state = state;
            this.zip = zip;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Location location = (Location) o;

            if (address1 != null ? !address1.equals(location.address1) : location.address1 != null) return false;
            if (address2 != null ? !address2.equals(location.address2) : location.address2 != null) return false;
            if (!city.equals(location.city)) return false;
            if (!state.equals(location.state)) return false;
            return zip.equals(location.zip);
        }

        @Override
        public int hashCode() {
            int result = address1 != null ? address1.hashCode() : 0;
            result = 31 * result + (address2 != null ? address2.hashCode() : 0);
            result = 31 * result + city.hashCode();
            result = 31 * result + state.hashCode();
            result = 31 * result + zip.hashCode();
            return result;
        }
    }
}
