package org.ace.insurance.medical.proposal;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.medical.policy.MedicalPolicyAttachment;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.MEDICALPROPOSALATTACHMENT)
@TableGenerator(name = "MEDICALPROPOSALATTACHMENT_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "MEDICALPROPOSALATTACHMENT_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class MedicalProposalAttachment {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "MEDICALPROPOSALATTACHMENT_GEN")
	private String id;
	private String name;
	private String filePath;

	@Embedded
	private UserRecorder recorder;

	@Version
	private int version;

	public MedicalProposalAttachment() {

	}

	public MedicalProposalAttachment(String name, String filePath) {
		this.name = name;
		this.filePath = filePath;
	}

	public MedicalProposalAttachment(MedicalPolicyAttachment policyAttach) {
		this.name = policyAttach.getName();
		this.filePath = policyAttach.getFilePath();
	}

	public MedicalProposalAttachment(MedProAttDTO medProAttDTO) {
		this.name = medProAttDTO.getName();
		this.filePath = medProAttDTO.getFilePath();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public UserRecorder getRecorder() {
		return recorder;
	}

	public void setRecorder(UserRecorder recorder) {
		this.recorder = recorder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + version;
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
		MedicalProposalAttachment other = (MedicalProposalAttachment) obj;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}