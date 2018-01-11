package finley.peter.nextgear.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name="contract")
public class Contract {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="contract_id", nullable=false)
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="business_number")
	private Long businessNumber;
	
	@Column(name="activation_date")
	private Date activationDate;
	
	@Column(name="amount_requested")
	private Integer amountRequested;
	
	@Column(name="status")
	private ContractStatus status;
	
	@Column(name="type")
	private ContractType type;

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getBusinessNumber() {
		return businessNumber;
	}

	public void setBusinessNumber(long businessNumber) {
		this.businessNumber = businessNumber;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Integer getAmountRequested() {
		return amountRequested;
	}

	public void setAmountRequested(int amountRequested) {
		this.amountRequested = amountRequested;
	}

	public ContractStatus getStatus() {
		return status;
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
	}

	public ContractType getType() {
		return type;
	}

	public void setType(ContractType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contract other = (Contract) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contract [id=" + id + ", name=" + name + ", businessNumber=" + businessNumber + ", activationDate="
				+ activationDate + ", amountRequested=" + amountRequested + ", status=" + status + ", type=" + type
				+ "]";
	}

}
