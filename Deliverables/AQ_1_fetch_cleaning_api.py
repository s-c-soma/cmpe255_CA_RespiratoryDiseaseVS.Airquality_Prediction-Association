import json
import requests
import pandas as pd
import pandas as np
import datetime
import calendar
import csv
from pandas.io.json import json_normalize  # package for flattening json in pandas df


def station_value(zone_name,index,response,dataType,year,writer):
    normalizedStation = json_normalize(response.json()['results'][index]['Stations'])
    data_val = normalizedStation['data']
    #print(normalizedStation)
    #print(data_val)
    #print(zone_name)

    bool_val = data_val.isnull()
    print(bool_val)
    for i in range(0, len(data_val)):
        if (bool_val[i] == True):
            print(i)
            data_val[i] = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        else:
            print(data_val[i])

    for i in range(0, len(data_val)):
        for j in range(0, 11):
            print("Zone Name: %s--Station Name: %s-- Month %d-- Val: %f" % (
            zone_name,normalizedStation['stationName'][i], j + 1, data_val[i][j]))
            station= normalizedStation['stationName'][i]
            value =data_val[i][j]
            month_num= j+1
            month_abre = datetime.date(2019, month_num, 1).strftime('%b')
            writer.writerow([dataType, year, month_num, month_abre, zone_name, station, value])


def main():
######### set params #########
    url = 'http://baaqmd-rtaqd.azurewebsites.net/rtaqd-api/data?authkey=JHRFBG84T548HBNFD38F0GIG05GJ48&lang=en'
    dataType = "aqi"
    dataView = "yearly"
    year= "2014"
    filepath= 'C:/Users/subar/Downloads/GitHub/cmpe255_Project/Dataset/AQData_monthly.csv'
    headers = ['AirQualityIndex', 'Year', 'MonthNo', 'MonthName', 'Zone', 'Station', 'Value']
    parameters = {"parameterId": "316", "dataType": dataType, "dataView": dataView, "startDate": year+"-01-01"}
##############################
    response = requests.post(url, json=parameters)
    # print('Created task. ID: {}'.format(response.json()))
    result_array = response.json()['results']

    with open(filepath, 'w', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(headers)
        for i in range(0, len(result_array)):
            #print(Result_array[i]['Zone Name'])
            station_value(result_array[i]['Zone Name'], i, response, dataType, year ,writer)



main()





