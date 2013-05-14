set terminal postscript enhanced color
set output 'SkipUnique_mem.ps'
set xlabel "Operation (100x) "
 set ylabel "Octets"
 set key inside left top
plot "FCTreeCycleBreaker-mem.data" with lines title "FCTreeCycleBreaker" lw 3 linecolor rgb "#29CC6A" smooth bezier, "FCTree-mem.data" with lines title "FCTree" lw 3 linecolor rgb "#8A2BE2" smooth bezier, "OTTreeWithoutGarbageO-mem.data" with lines title "OTTree" lw 3 linecolor rgb "#FF0000" smooth bezier, "TreeOPTWithoutGarbageO-mem.data" with lines title "TreeOPT" lw 3 linecolor rgb "#1E90FF" smooth bezier, "WordTree-mem.data" with lines title "WordTree" lw 3 linecolor rgb "#00000" smooth bezier, "xmlProf-mem.data" with lines title "xml" lw 3 linecolor rgb "#777777" smooth bezier
