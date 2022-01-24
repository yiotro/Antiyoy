package yio.tro.antiyoy.menu.diplomatic_exchange;

import yio.tro.antiyoy.gameplay.diplomacy.exchange.ExchangeType;
import yio.tro.antiyoy.stuff.object_pool.ObjectPoolYio;

import java.util.HashMap;

public class ArgumentViewFactory {


    private static ArgumentViewFactory instance;
    HashMap<ExchangeType, ObjectPoolYio<AbstractExchangeArgumentView>> mapPools;


    public ArgumentViewFactory() {
        mapPools = new HashMap<>();
    }


    public static void initialize() {
        instance = null;
    }


    public static ArgumentViewFactory getInstance() {
        if (instance == null) {
            instance = new ArgumentViewFactory();
        }

        return instance;
    }


    public void onArgumentViewDestroyed(AbstractExchangeArgumentView argumentView) {
        ExchangeType exchangeType = argumentView.getExchangeType();
        ObjectPoolYio<AbstractExchangeArgumentView> pool = getPool(exchangeType);

        pool.addWithCheck(argumentView);
    }


    private ObjectPoolYio<AbstractExchangeArgumentView> getPool(ExchangeType exchangeType) {
        if (mapPools.containsKey(exchangeType)) {
            return mapPools.get(exchangeType);
        }
        return createPool(exchangeType);
    }


    private ObjectPoolYio<AbstractExchangeArgumentView> createPool(final ExchangeType exchangeType) {
        return new ObjectPoolYio<AbstractExchangeArgumentView>() {
            @Override
            public AbstractExchangeArgumentView makeNewObject() {
                switch (exchangeType) {
                    default:
                    case remove_black_mark:
                    case stop_war:
                        return new AvBlank();
                    case nothing:
                        return new AvNothing();
                    case lands:
                        return new AvLands();
                    case war_declaration:
                        return new AvWarDeclaration();
                    case money:
                        return new AvMoney();
                    case dotations:
                        return new AvDotations();
                    case friendship:
                        return new AvFriendship();
                }
            }
        };
    }


    public AbstractExchangeArgumentView createArgumentView(ExchangeProfitView profitView, ExchangeType exchangeType) {
        AbstractExchangeArgumentView next = getPool(exchangeType).getNext();
        next.setExchangeProfitView(profitView);
        return next;
    }
}
