library(tsm)
library(vars)
library(mFilter)

dat <- read.csv(file = "../Datasets/merged_monthly.csv")
dat

//['avg_aqi', 'A(H1)', 'A(UnabletoSubtype)', 'A(H3)', 'A(H1N1)pdm09',
   'A(SubtypingNotPerformed)', 'B(VictoriaLineage)', 'B(YamagataLineage)',
   'B(LineageUnspecified)']

AirQuality <- ts(dat$avg_aqi, start = c(1999, 1), freq = 10)
Virus <- ts(dat$A_H3, start = c(1999, 1), freq = 10)

plot(cbind(AirQuality, Virus))

adf.AirQuality <- ur.df(AirQuality, type = "trend", selectlags = "AIC")
summary(adf.AirQuality)

dat.bv <- cbind(AirQuality, Virus)
colnames(dat.bv) <- c("AirQuality", "Virus")

info.bv <- VARselect(dat.bv, lag.max = 12, type = "const")
info.bv$selection

bv.est <- VAR(dat.bv, p = 12, type = "const", season = NULL, 
              exog = NULL)
summary(bv.est)

bv.serial <- serial.test(bv.est, lags.pt = 12, type = "PT.asymptotic")
bv.serial
plot(bv.serial, names = "AirQuality")
plot(bv.serial, names = "Virus")


bv.arch <- arch.test(bv.est, lags.multi = 12, multivariate.only = TRUE)
bv.arch


bv.norm <- normality.test(bv.est, multivariate.only = TRUE)
bv.norm


#confidence level
bv.cusum <- stability(bv.est, type = "OLS-CUSUM")
plot(bv.cusum)

#Granger causality, IRFs and variance decompositions
bv.cause.AirQuality <- causality(bv.est, cause = "AirQuality")
bv.cause.AirQuality


bv.cause.Virus <- causality(bv.est, cause = "Virus")
bv.cause.Virus

irf.AirQuality <- irf(bv.est, impulse = "Virus", response = "AirQuality", 
                      n.ahead = 40, boot = TRUE)
plot(irf.AirQuality, ylab = "ouput", main = "Seasonal Effect 1")


irf.Virus <- irf(bv.est, impulse = "AirQuality", response = "Virus", 
                 n.ahead = 40, boot = TRUE)
plot(irf.Virus, ylab = "Value", main = "Seasonal Effect 2")


irf.une_un <- irf(bv.est, impulse = "Virus", response = "Virus", 
                  n.ahead = 40, boot = TRUE)
plot(irf.une_un, ylab = "Value", main = "Seasonal Effect 3")


#forecast error variance composition
bv.vardec <- fevd(bv.est, n.ahead = 10)
plot(bv.vardec)


#forecasting

predictions <- predict(bv.est, n.ahead = 8, ci = 0.95)
plot(predictions, names = "AirQuality")

plot(predictions, names = "Virus")


#
fanchart(predictions, names = "AirQuality")
fanchart(predictions, names = "Virus")