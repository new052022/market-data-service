package monaco.bot.marketdata.dto.binance.user_trades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTradesHistoryResponseDto {

    // Покупатель (true - покупка, false - продажа)
    private boolean buyer;

    // Комиссия за сделку
    private BigDecimal commission;

    // Актив, в котором взимается комиссия
    private String commissionAsset;

    // ID сделки
    private long id;

    // Флаг мейкера (true - мейкер, false - тейкер)
    private boolean maker;

    // ID ордера
    private long orderId;

    // Цена сделки
    private BigDecimal price;

    // Количество базового актива
    private BigDecimal qty;

    // Общая сумма в квотируемой валюте
    private BigDecimal quoteQty;

    // Реализованная прибыль/убыток
    private BigDecimal realizedPnl;

    // Направление сделки (SELL или BUY)
    private String side;

    // Сторона позиции (LONG или SHORT)
    private String positionSide;

    // Торговая пара
    private String symbol;

    // Время совершения сделки (Unix timestamp в миллисекундах)
    private long time;

    private String exchange;

}
