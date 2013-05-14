#!/bin/bash
function mkfig {
echo "set terminal postscript enhanced color" > $1.gnuplot
echo "set output 'SkipUnique_$1.ps'" >> $1.gnuplot

echo "set xlabel \"Operation (100x) \"">>$1.gnuplot
echo " set ylabel \"Time (in microseconds) \"" >> $1.gnuplot
if [ "$2" == "" ]
then 
echo "set logscale y">>$1.gnuplot
else
echo "set yr [0:$2]">>$1.gnuplot
fi

echo " set key inside left top">>$1.gnuplot

color=("#29CC6A" "#8A2BE2" "#FF0000" "#1E90FF" "#00000" "#dcff00" "#7100ff" "#00a3ff" "#ff7200")
file=(`ls *$1*.data`)
files=${#file[*]}
out="plot "

for p in ${!file[*]}
do 
out="$out \"${file[p]}\" with lines title \"${file[p]}\" lw 3  linecolor rgb \"${color[p]}\" smooth bezier,"
done

echo ${out%,} >> $1.gnuplot
gnuplot $1.gnuplot
}

mkfig loc
mkfig mem
mkfig dist

