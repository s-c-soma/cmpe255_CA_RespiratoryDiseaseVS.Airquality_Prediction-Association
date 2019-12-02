library(tsm)
library(vars)
library(mFilter)

dat <- read.csv(file = "C:/Users/subar/Downloads/GitHub/cmpe255_Project/Cmpe255_Project/Datasets/merged_monthly.csv")
dat

//['avg_aqi', 'A(H1)', 'A(UnabletoSubtype)', 'A(H3)', 'A(H1N1)pdm09',
   'A(SubtypingNotPerformed)', 'B(VictoriaLineage)', 'B(YamagataLineage)',
   'B(LineageUnspecified)']

gdp <- ts(dat$avg_aqi, start = c(1999, 1), freq = 10)
une <- ts(dat$A_H3, start = c(1999, 1), freq = 10)

plot(cbind(gdp, une))

adf.gdp <- ur.df(gdp, type = "trend", selectlags = "AIC")
summary(adf.gdp)

dat.bv <- cbind(gdp, une)
colnames(dat.bv) <- c("gdp", "une")

info.bv <- VARselect(dat.bv, lag.max = 12, type = "const")
info.bv$selection

bv.est <- VAR(dat.bv, p = 12, type = "const", season = NULL, 
              exog = NULL)
summary(bv.est)

bv.serial <- serial.test(bv.est, lags.pt = 12, type = "PT.asymptotic")
bv.serial
plot(bv.serial, names = "gdp")
plot(bv.serial, names = "une")