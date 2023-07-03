package com.springboot.SmartContactManager.Model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

@Entity
public class Contact {
	@Id
	private int cid;
	@NotBlank(message = "User name can't be blank")
	private String name;
	private String nickName;
	private String work;
	@Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
			+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", message = "It allows numeric values from 0 to 9.\r\n"
					+ "Both uppercase and lowercase letters from a to z are allowed.\r\n"
					+ "Allowed are underscore “_”, hyphen “-“, and dot “.”\r\n"
					+ "Dot isn't allowed at the start and end of the local part.\r\n"
					+ "Consecutive dots aren't allowed.")
	private String email;
	@Size(min = 10, max = 10, message = "Contact no should be exact 10 characters")
	@Pattern(regexp = "(^$|[0-9]{10})", message = "Only numbers are allowed")
	private String phone;
	private String image;
	@Column(length = 5000)
	private String dscription;
	@ManyToOne
	private User user;

	public Contact() {
		super();
	}

	public Contact(int cid, String name, String nickName, String work, String email, String phone, String image,
			String dscription) {
		super();
		this.cid = cid;
		this.name = name;
		this.nickName = nickName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.dscription = dscription;
	}

	public Contact(int cid, String name, String nickName, String work, String email, String phone, String image,
			String dscription, User user) {
		super();
		this.cid = cid;
		this.name = name;
		this.nickName = nickName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.dscription = dscription;
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDscription() {
		return dscription;
	}

	public void setDscription(String dscription) {
		this.dscription = dscription;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cid, dscription, email, image, name, nickName, phone, work);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		return cid == other.cid && Objects.equals(dscription, other.dscription) && Objects.equals(email, other.email)
				&& Objects.equals(image, other.image) && Objects.equals(name, other.name)
				&& Objects.equals(nickName, other.nickName) && Objects.equals(phone, other.phone)
				&& Objects.equals(work, other.work);
	}

	@Override
	public String toString() {
		return "Contact [cid=" + cid + ", name=" + name + ", nickName=" + nickName + ", work=" + work + ", email="
				+ email + ", phone=" + phone + ", image=" + image + ", dscription=" + dscription + "]";
	}

}
