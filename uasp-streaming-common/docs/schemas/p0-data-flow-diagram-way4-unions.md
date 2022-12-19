#Описание DataFlow диаграммы обработки потоков данных

##Потоки данных связанные с курсами валют

P0.OUTER.CBR.RATES - Топик внешней системы сообщений с курсами всех валют в одном сообщении 

p0_ivr__uasp_realtime__outer_cbr_rates__json - Топик предназначен для приёма сообщений с курсами всех валют в одном сообщении из внешней системы, необходим для использования несколькими сервисами одновременно в одной системе в нашем случае УАСП

p0_ivr__uasp_realtime__outer_cbr_rate__uaspdto - Топик с курсами валют по одной валюте в одном сообщении в формате UaspDto

p0_ivr__uasp_realtime__outer_cbr_rate__status - Топик с информацией об успешности обработки валюты, что означает валюта сохранена в состояние Flink и готова для обогащения потока way4

p0_ivr__uasp_realtime__mdm_enrichment__uaspdto - Топик way4 обогащённый курсами валют

P0.OUTER.CBR.RATES.ACK - Топик внешней системы для подтверждения результатов обработки сообщений с курсами валют из топика P0.OUTER.CBR.RATES

p0_ivr__uasp_realtime__outer_cbr_rate__ask - Дублирующий топик P0.OUTER.CBR.RATES.ACK для обеспечения тех.поддержки УАСП информацией о результатах обработки потока курса валют из внешней системы топика P0.OUTER.CBR.RATES   

###Сервисы обогащения курсами валю потока way4

####Сервис приёма курса валют
P0.OUTER.CBR.RATES -> unp_convertor_outer_cbr_rates -> p0_ivr__uasp_realtime__outer_cbr_rates__json

####Сервис парсинга и дробления на отдельные курсы валют
p0_ivr__uasp_realtime__outer_cbr_rates__json -> uasp_streaming_input_convertor_cbr_rates -> p0_ivr__uasp_realtime__outer_cbr_rate__uaspdto

####Сервис обогащения потока way4 курсами валют
p0_ivr__uasp_realtime__input_converter__way4_issuing_operation__json, p0_ivr__uasp_realtime__outer_cbr_rate__uaspdto -> uasp_streaming_main_input -> p0_ivr__uasp_realtime__mdm_enrichment__uaspdto, p0_ivr__uasp_realtime__outer_cbr_rate__status

####Сервис подтверждения получения курсов валют
P0.OUTER.CBR.RATES, p0_ivr__uasp_realtime__outer_cbr_rate__status -> uasp_streaming_exchange_rates_status -> P0.OUTER.CBR.RATES.ACK, p0_ivr__uasp_realtime__outer_cbr_rate__ask 


### Потоки данных ипотечных клиентов
p0_ivr__uasp_realtime__input_converter__mortgage__uaspdto
p0_ivr__uasp_realtime__input_converter__mortgage__status
p0_ivr__uasp_realtime__filter__uaspdto__dlq
p0_ivr__uasp_realtime__filter__uaspdto__filter
p0_ivr__uasp_realtime__bussiness_rules__uaspdto__dlq

