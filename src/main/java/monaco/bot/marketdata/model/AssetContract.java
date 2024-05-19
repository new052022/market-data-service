package monaco.bot.marketdata.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asset_contract")
public class AssetContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "quantity_precision")
    private Double quantityPrecision;

    @Column(name = "price_precision")
    private Double pricePrecision;

    @Column(name = "fee_rate")
    private Double feeRate;

    @Column(name = "maker_fee_rate")
    private Double makerFeeRate;

    @Column(name = "taker_fee_rate")
    private Double takerFeeRate;

    @Column(name = "trade_min_limit")
    private Double tradeMinLimit;

    @Column(name = "trade_min_quantity")
    private Double tradeMinQuantity;

    @Column(name = "trade_min_usdt")
    private Double tradeMinUSDT;

    @Column(name = "max_long_leverage")
    private Long maxLongLeverage;

    @Column(name = "max_short_leverage")
    private Long maxShortLeverage;

    @Column(name = "currency")
    private String currency;

    @Column(name = "asset")
    private String asset;

    @Column(name = "binance_contract_type")
    private String binanceContractType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_id")
    private Exchange exchange;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "assetContract", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    private Set<FilterType> filters;

}
