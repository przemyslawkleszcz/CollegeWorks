import subprocess
from random import uniform

PATH = "data.txt"
OUT = "testfile.ps"

HUNDRED_PERCENT = 100
ROUND_ANGLE = 360

chartCenter = [300, 650]
chartRadius = 100
fontSizes = [20, 16]
legendUpperMargin = 60
chartColours = {}
chartData = {}


def fetchFromFile():
    data = []

    try:
        with open(PATH, 'r') as file:
            for line in file:
                line = line.lower()
                data.append(line)
    except IOError:
        print("Cannot open the file")

    return data


def divide(data):
    percentageSum = 0

    for line in data:
        elements = line.split()

        key = elements[0]

        for i in range(1, len(elements) - 1):
            key += " " + elements[i]

        elements[len(elements) - 1] = elements[len(elements) - 1].replace(',', '.')

        chartData[key] = [0.0, float(elements[len(elements) - 1])]
        percentageSum += float(elements[len(elements) - 1])

    findAngleAndPercantageShare(percentageSum)


def findAngleAndPercantageShare(percentageSum):
    for series, data in chartData.items():
        percentageShare = (data[1] / percentageSum) * HUNDRED_PERCENT
        angle = (data[1] / percentageSum) * ROUND_ANGLE

        chartData[series] = [angle, percentageShare]


def draw():
    for series, data in chartData.items():
        rgbColor = []
        rgbColor.append(round(uniform(0, 1), 2))
        rgbColor.append(round(uniform(0, 1), 2))
        rgbColor.append(round(uniform(0, 1), 2))
        chartColours[series] = rgbColor


def generatePieChart(output):
    startAngle = 0
    endAngle = None

    for series, data in chartData.items():
        endAngle = startAngle + data[0]

        output.write("%%%" + series + "\n")
        output.write(
            "newpath\n" + str(chartCenter[0]) + " " + str(chartCenter[1]) + " moveto\n")
        output.write(str(chartCenter[0]) + " " + str(chartCenter[1]) + " " +
                     str(chartRadius) + " " + str(startAngle) + " " + str(endAngle) + " arc\n")
        output.write("closepath\ngsave\n")

        RGB = chartColours[series]
        output.write(str(RGB[0]) + " " + str(RGB[1]) + " " + str(RGB[2]) +
                     " setrgbcolor\nfill\ngrestore\nstroke\n\n")

        startAngle = endAngle


def generateLegend(output):
    output.write("\n/Verdana findfont\n" + str(fontSizes[1]) + " scalefont\nsetfont\n")

    Y = ((chartCenter[1] - chartRadius - legendUpperMargin) - fontSizes[0])

    for seria, dane in chartData.items():
        output.write(str((chartCenter[0] - chartRadius)) + " " + str(Y) + " moveto\n")

        RGB = chartColours[seria]
        output.write(str(RGB[0]) + " " + str(RGB[1]) + " " + str(RGB[2]) + " setrgbcolor\n")
        output.write("(" + seria + ") show\n")
        output.write("0 0 0 setrgbcolor\n")

        text = "("

        for i in range(0, 10 - len(seria)):
            text += " "
        text += "{0:.2f}) show\n\n".format(dane[1])
        output.write(text)

        Y -= fontSizes[1]


def generatePlotFile(outputFile):
    draw()
    try:
        with open(outputFile, 'w') as file:
            file.write("%! PS – Adobe\n")
            file.write("\n")
            generatePieChart(file)
            generateLegend(file)
            file.write("showpage\n")
    except IOError:
        print("Cannot write to file")


fileData = fetchFromFile()
divide(fileData)
generatePlotFile(OUT)