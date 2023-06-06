package store.juin.api.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private Integer rate; // 1 to 5

    @Column(columnDefinition = "TEXT")
    private String content;

    public void updateReview(Integer rate, String content) {
        this.rate = rate;
        this.content = content;
    }
}