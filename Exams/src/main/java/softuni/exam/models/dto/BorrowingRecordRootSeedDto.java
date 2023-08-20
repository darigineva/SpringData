package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "borrowing_records")
@XmlAccessorType(XmlAccessType.FIELD)
public class BorrowingRecordRootSeedDto {
    @XmlElement(name = "borrowing_record")
    List<BorrowingRecordSeedDto> borrowingRecordSeedDtoList;

    public BorrowingRecordRootSeedDto() {
    }

    public List<BorrowingRecordSeedDto> getBorrowingRecordSeedDtoList() {
        return borrowingRecordSeedDtoList;
    }

    public void setBorrowingRecordSeedDtoList(List<BorrowingRecordSeedDto> borrowingRecordSeedDtoList) {
        this.borrowingRecordSeedDtoList = borrowingRecordSeedDtoList;
    }
}
