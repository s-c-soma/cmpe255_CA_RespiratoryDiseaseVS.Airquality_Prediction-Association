import requests
import csv


year= "2017"
url = 'https://trackingcalifornia.org/ncdm/chart/asthma?EVENT=ASED&TYPE=CR10K&RACE=TOTL&AGE=TOTL&SEX=TOTL&YEAR={0}&MODEL=CONV&GEO=CTY'.format(year)
filepath= 'C:/Users/subar/Downloads/CMPE-255 Sec 99 - Data Mining/Home Works/Asthmadata_by_county_2014_2017.csv'
headers = ['CountyName', 'CountyCode', 'Year','Age-adjusted_Rates_Per_10k', 'Lower_95%_Limit', 'Upper_95%_Limit', 'Total']


response = requests.post(url)
print('response: {}'.format(response.json()))
result_array = response.json()['rows']
with open(filepath, 'a', newline='') as f:
    writer = csv.writer(f)
    #writer.writerow(headers)
    for a in result_array:
        print(a['countyName'], a['fipsCode'], year, a['col1'],a['col2'], a['col3'], a['col4'])
        writer.writerow([a['countyName'], a['fipsCode'], year, a['col1'],a['col2'], a['col3'], a['col4']])






