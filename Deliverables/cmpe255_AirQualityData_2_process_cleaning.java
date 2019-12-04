#include<stdio.h>
#include<ctype.h>
#include<string.h>
#include<stdlib.h>
#include<algorithm>
#include<vector>
#include<string>
#include<map>

using namespace std;

vector< pair<string, int> > state_year_pairs;
map< pair<string, int>, string> state_year_to_filename;
map< pair<string, int>, vector< pair<int, double> > > state_year_month_value;

void process_name(string filename) {
	char state_name[500];
	int year = 0;
	int i = 0;
	while (filename[i] != '-' && filename[i] != '_' && i < filename.size()) {
		state_name[i] = filename[i];
		i++;
	}
	state_name[i] = 0;
	
	while (i+2 < filename.size()) {
		if (filename[i] == 'Y' && filename[i+1] == 'R' && filename[i+2] == '_')
			break;
		else
			i++;
	}
	i += 3;
	while (i < filename.size() && isdigit(filename[i])){
		year = year * 10 + filename[i] - '0';
		i++;	
	}

	//printf("%s %d\n", state_name, year);
	pair<string, int> psi = make_pair((string)state_name, year);
	state_year_pairs.push_back(psi);
	state_year_to_filename[psi] = filename;
	vector<pair<int, double> > vpid;
	state_year_month_value[psi] = vpid;
}

void get_all_names(string directory) {
	char system_call_string[500];
	char buf[500];
	sprintf(system_call_string, "ls %s > all_filenames.txt", directory.c_str());
	int ret_system = system(system_call_string);
	
	state_year_pairs.clear();
	state_year_to_filename.clear();
	FILE* fpi = fopen("all_filenames.txt", "r");
	while(fgets(buf, sizeof(buf), fpi)) {
		for (int i = 0; buf[i]; i++)
			if (buf[i] == '\n') {
				buf[i] = 0;
				break;
			}
		process_name(buf);
	}
	fclose(fpi);
}

int leap_year(int year) {
	if (year % 400 == 0) return 1;
	if (year % 100 == 0) return 0;
	if (year % 4 == 0)
		return 1;
	else
		return 0;
}

vector<double> process_line(string line) {
	int i;

	int comma = 0;
	for (i = 0; i < line.size(); i++) {
		if (line[i] == ',')
			comma++;
	}
	if (comma < 12) {
		line.append(12 - comma, ',');
		//printf("here\n");
	}

	for (i = 0; i < line.size(); i++) {
		if (line[i] == ',' && ((i+1) == line.size() || line[i+1] == ',' || line[i+1] == '\n')) {
			line.insert(i + 1, 1,'0');
		}
	}

	char buf[500];
	sprintf(buf, "%s", line.c_str());
	for (i = 0; buf[i]; i++)
		if(buf[i] == ',')
			buf[i] = ' ';

	int bs = 0, db, idx;
	sscanf(buf + bs, "%d%n", &idx, &db);
	bs += db;

	vector<double> ret;
	ret.clear();
	double val;
	while(sscanf(buf + bs, "%lf%n", &val, &db) == 1) {
		ret.push_back(val);
		bs += db;
	}
	while (ret.size() < 12) {
		ret.push_back(0);
	}
	return ret;
}

void process_one_file(string directory, string county, int year) {
		// Jan, Feb, Mar, Ap, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
	int days[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	pair<string, int> pcy = make_pair(county, year);
	char filename[500], buf[500], header[500];
	sprintf(filename, "%s%s", directory.c_str(), state_year_to_filename[pcy].c_str());
	FILE* fpi = fopen(filename, "r");
	// printf(">>> %s\n", filename);

	fgets(buf, sizeof(buf), fpi);
	sprintf(header, "%s", buf);

	vector<double> sum(12, 0); // 12 months
	for (int i = 0; i < 31; i++) {
		fgets(buf, sizeof(buf), fpi);
		vector<double> values = process_line((string)buf);
		if (values.size() != 12)
			continue;
		for (int j = 0; j < 12; j++)
			sum[j] += values[j];
	}
	fclose(fpi);

	for (int i = 0; i < 12; i++) {
		int d = days[i];
		if (i == 1) {	// Feb
			d += leap_year(year);
		}
		sum[i] /= d;
	}	

	char out_filename[500];
	sprintf(out_filename, "%s", state_year_to_filename[pcy].c_str());
	int idx = strlen(out_filename) - 1;
	while(idx >= 0 && out_filename[idx] != '.')
		idx--;
	out_filename[idx + 1] = 't'; out_filename[idx + 2] = 'x'; out_filename[idx + 3] = 't';

	FILE* fpo = fopen(out_filename, "w");
	fprintf(fpo, "County,Year,Month,Value(Avg)\n");
	for (int i = 0; i < 12; i++) {
		fprintf(fpo, "%s,%d,%d,%lf\n", county.c_str(), year, i + 1, sum[i]);
		printf("%s,%d,%d,%lf\n", county.c_str(), year, i + 1, sum[i]);
	}
	fclose(fpo);
}

int main() {
	string directory = "data-AQ/";
	get_all_names(directory);

	for (int i = 0; i < state_year_pairs.size(); i++) {
		process_one_file(directory, state_year_pairs[i].first, state_year_pairs[i].second);
	}	
	return 0;
}
