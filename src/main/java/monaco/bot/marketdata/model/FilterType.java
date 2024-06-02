package monaco.bot.marketdata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "filter_type")
public class FilterType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_contract_id")
    private AssetContract assetContract;

    @Column(name = "filter_type")
    private String FilterType;

    @Column(name = "max_price")
    private Double maxPrice;

    @Column(name = "min_price")
    private Double minPrice;

    @Column(name = "tick_size")
    private Double tickSize;

    @Column(name = "max_qty")
    private Double maxQty;

    @Column(name = "min_qty")
    private Double minQty;

    @Column(name = "step_size")
    private Double stepSize;

    @Column(name = "limit_size")
    private Double limit;

    @Column(name = "notional")
    private Double notional;

    @Column(name = "multiplier_up")
    private Double multiplierUp;

    @Column(name = "multiplier_down")
    private Double multiplierDown;

    @Column(name = "multiplier_decimal")
    private Double multiplierDecimal;

}
