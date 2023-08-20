package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)

public class BorrowingRecordSeedDto {
//    borrow_date>2020-01-13</borrow_date>
//        <return_date>2022-09-11</return_date>
//        <book>
//    <title>The Lord of the Rings</title>
//        </book>
//        <member>
//            <id>27</id>
//        </member>
//        <remarks>

    @XmlElement(name = "borrow_date")
    @NotNull
    private String borrowDate;
    @XmlElement(name = "return_date")
    @NotNull
    private String returnDate;
    @XmlElement
    @Size(min = 3, max = 100)
    private String remarks;
    @XmlElement
    @NotNull
    private BookTitleDto book;
    @XmlElement
    @NotNull
    private MemberIdDto member;

    public BorrowingRecordSeedDto() {
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BookTitleDto getBook() {
        return book;
    }

    public void setBook(BookTitleDto book) {
        this.book = book;
    }

    public MemberIdDto getMember() {
        return member;
    }

    public void setMember(MemberIdDto member) {
        this.member = member;
    }
}
