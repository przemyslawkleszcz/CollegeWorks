n = int(input("Podaj n "))
m = int(input("Podaj m "))

dn = 500/n
dm = 500/m
scale = 500

file = open("testfile.ps", "w")

file.write("/square{" + str(dn) + " 0 rlineto 0 " + str(dm) + " rlineto -" + str(dn) + " 0 rlineto closepath 0.2 0.2 0.2 setrgbcolor fill} def\n");
file.write("/square2{" + str(dn) + " 0 rlineto 0 " + str(dm) + " rlineto -" + str(dn) + " 0 rlineto closepath 0.7 0.7 0.7 setrgbcolor fill} def\n");

sideX = 0;
sideY = 0;
for i in range(0, m):
    sideX += dn
    for j in range(0, n):
        sideY += dm
        file.write(str(sideX) + " " + str(sideY) + " moveto square" +  "2" if ((j + i) % 2 != 0) else "" + "\n")
    sideY = 0;
    file.write("\n");


file.write("stroke\n")
file.write("showpage\n")

file.close()