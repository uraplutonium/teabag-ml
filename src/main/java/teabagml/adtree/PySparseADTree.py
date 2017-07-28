'''
Reproduced on Jul 19, 2017
@author: Felix
'''

arityLength = 0
arityList = []
recordsLength = 0
data = None
adtree = None

def loadFile(filePath):
    global data
    global arityLength
    global airtyList
    global recordsLength
    # initialise the data, arityList, arityLength and recordsLength
    initRecord([filePath])
    data.displayAll()
    return (data, arityLength, arityList, recordsLength)

def countInFile(fileTuple, query):
    global data
    global arityLength
    global airtyList
    global recordsLength
    data = fileTuple[0]
    arityLength = fileTuple[1]
    arityList = fileTuple[2]
    recordsLength = fileTuple[3]
    return count(query)
    

def makeSparseADTree(filePath):
    global data
    global recordsLength
    global adtree
    # initialise the data, arityList, arityLength and recordsLength
    initRecord([filePath])
    data.displayAll()

    # make the sparse adtree
    recordNums = [num for num in range(1, recordsLength+1)]
    adtree = ADNode(1, recordNums)
    return (adtree, arityLength, arityList, recordsLength)

'''
@param adnode the root node of the adtree
@param attrList the list containing the index of current node's parents (counts from 0)
@return the contingency table 
'''
def makeContab(adtreeTuple, attrList):
    global arityLength
    global arityList
    global recordsLength
    global adtree
    adtree = adtreeTuple[0]
    arityLength = adtreeTuple[1]
    arityList = adtreeTuple[2]
    recordsLength = adtreeTuple[3]

    contab = ContingencyTable([v+1 for v in attrList], adtree)
    return (contab, arityLength, arityList, recordsLength)

'''
@param contab the contingency table
@param query the query like
'''
def getCount(contabTuple, query):
    global arityLength
    global arityList
    global recordsLength
    contab = contabTuple[0]
    arityLength = contabTuple[1]
    arityList = contabTuple[2]
    recordsLength = contabTuple[3]

    count = contab.getCount(query)
    return count

'''
FullContingencyTable
'''
class ContingencyTable(list):
    
    def __init__(self, attributeList, ADN):
        self.__attributeList = attributeList
        self.__dimension = len(attributeList)
        global arityList

        if ADN:
            if attributeList:   # AD-node that has Vary node children
                VN = ADN.getVNChild(attributeList[0])
                MCV = VN.getMCV()
                CTList = []
    
                for eachAttributeValue in range(1, arityList[attributeList[0]-1]+1):       
                    if eachAttributeValue != MCV:
                        childADN = VN.getADNChild(eachAttributeValue)
                        CTList.append(ContingencyTable(attributeList[1:], childADN))
                    else:
                        CTList.append(None) # leave a position for MCV
                            
                # calculate the contingency table for MCV
                CTList[MCV-1] = ContingencyTable(attributeList[1:], ADN)
                for i, eachNotMCVCT in enumerate(CTList):
                    if i+1 != MCV:
                        CTList[MCV-1].subInRow(eachNotMCVCT)
                
                self.extend(ContingencyTable.concatenate(attributeList[0], CTList))
            else:   # leaf AD-node
                self.append([ADN.getCount()])
        else:
            if attributeList:   # zero AD-node with subtree
                CTList = []
                for eachAttributeValue in range(1, arityList[attributeList[0]-1]+1):          
                    CTList.append(ContingencyTable(attributeList[1:], None))
                self.extend(ContingencyTable.concatenate(attributeList[0], CTList))
            else:   # zero leaf AD-node
                self.append([0])

    @staticmethod
    def concatenate(attributeNum, CTList):
        '''Make the concatenation of all conditional contingency tables for attributeNum'''
        global arityList
        resultCT = []
        for eachAttributeValue in range(1, arityList[attributeNum-1]+1):
            for eachRow in CTList[eachAttributeValue-1]:
                resultCT.append([eachAttributeValue] + eachRow)
        return resultCT
    
    def subInRow(self, other):      
        for index, row in enumerate(self):
            row[-1] -= other[index][-1]
        
    def getCount(self, query):
        rowNum = 0
        for i, eachAttributeValue in enumerate(query):
            while(self[rowNum][i] != eachAttributeValue):
                rowNum+=1
        return self[rowNum][-1]
    
'''
SparseADTree
'''
class ADNode(object):
    '''
    The value of attribute this ADN is represented as the index of this ADN in its parent VN's children list
    eg. The index of this ADN in parent VN's children list is 2(since the index starts from 0),
        if this ADN is representing ai=3
    '''
    
    def __init__(self, startAttributeNum, recordNums):
        '''Make a ADNode and its children nodes'''
        global arityLength
        self.__count = len(recordNums)
        self.__children = [None]*(arityLength+1-startAttributeNum)
        for eachAttributeNum in range(startAttributeNum, arityLength+1):
            self.__children[eachAttributeNum-startAttributeNum] = VaryNode(eachAttributeNum, recordNums)
    
    def getCount(self):
        return self.__count
    
    def getVNChild(self, attributeNum):
        '''attributeNum ranges from 1(NOT 0) to the max attribute number'''
        global arityLength
        return self.__children[attributeNum + len(self.__children) - arityLength - 1]
        
class VaryNode(object):
    '''
    The attribute number is represented as the index of this VN in its parent ADN's children list
    eg. The index of this VN in parent ADN's children list is 2(since index starts from 0),
        if this VN is representing a3.
    '''
    
    def __init__(self, attributeNum, recordNums):
        '''Make a Vary Node and its children nodes'''
        global arityList
        self.__MCV = 0
        self.__children = [None]*(arityList[attributeNum-1])
        
        #Initialises the childNum list for each attribute value
        childNums = [[] for eachAttributeValue in range(arityList[attributeNum-1])]
        
        #This loop puts the amount for each attribute value into childNums list from the recordsTable
        for eachRecordNum in recordNums:
            value = getRecord(eachRecordNum-1, attributeNum-1)
            childNums[value-1].append(eachRecordNum)

        #Get the MCV from childNums
        self.__MCV = childNums.index(max(childNums, key=len))+1
        
        #This loop creates AD-Nodes for each attribute value and attaches them to this Vary Node
        for eachAttributeValue in range(1, arityList[attributeNum-1]+1):
            if eachAttributeValue != self.__MCV and childNums[eachAttributeValue-1]:
                self.__children[eachAttributeValue-1] = ADNode(attributeNum+1, childNums[eachAttributeValue-1])
                
    def getMCV(self):
        return self.__MCV
    
    def getADNChild(self, attributeValue):
        '''attributeValue ranges from 1(NOT 0) to the Record.arityList[attributeNum]'''
        return self.__children[attributeValue-1]

'''
FileRecord
'''
def initRecord(args):
    global data
    global arityList
    global arityLength
    global recordsLength
    data = dataset(args[0], True)
    arityList = data.getArityList()
    arityLength = len(arityList)
    recordsLength = data.getDataNum()
   
def getRecord(row, column):
    global data
    #print(data.getEntry(row, column))
    return data.getEntry(row, column)


def count(query):
    global data
    return data.count(query)
    
'''
Dataset
'''
class dataset:
    def __init__(self, filePath, symbolic=False):
        self.__arityNameList = []
        self.__arityList = []
        self.__data = []
        self.__arityLength = 0
        self.__dataNum = 0
        self.__arities = [] # arityies is also change to a list of list, rather than a list of sets
        #self.__types = set()
        self.__types = [] # use a list to keep the same orders with the Dataset.java
        
        file = open(filePath)
        for i, eachline in enumerate(file):
            if i==0:
                # the first line
                self.__arityNameList = eachline[:-1].split(',')
                self.__arityLength = len(self.__arityNameList)
                for i in range(0, self.__arityLength):
                    #self.__arities.append(set())
                    self.__arities.append([])
            else:
                # the data field
                self.__data.append(eachline[:-1].split(','))
                for j, eachValue in enumerate(self.__data[-1]):
                    if eachValue not in self.__arities[j]:
                        self.__arities[j].append(eachValue)

        self.__dataNum = len(self.__data)
        for eachArity in self.__arities:
             self.__arityList.append(len(eachArity))

        # convert __data and __arities to symbolic
        if symbolic:
            for i, eachEntry in enumerate(self.__data):
                for j, eachValue in enumerate(eachEntry):
                    self.__data[i][j] = self.__arities[j].index(eachValue)+1
            for j in range(0, self.__arityLength):
                self.__arities[j] = range(1, self.__arityList[j]+1)

    def getArityList(self):
        return self.__arityList

    def getTypeList(self):
        return self.__typeList
    
    def count(self, query):
        if '*' not in query:
            return self.__fullCount(query)
        else:
            for i, eachAttribute in enumerate(query):
                if eachAttribute != '*':
                    continue
                else:
                    c = 0
                    for eachValue in self.__arities[i]:
                        tmpQuery = query[:i] + [eachValue] + query[i+1:]
                        #print(tmpQuery)
                        c = c+self.count(tmpQuery)
                    return c
                
    def __fullCount(self, query):
        return self.__data.count(query)
    
    def getArityLength(self):
        return self.__arityLength
    
    def getEntry(self, row, column):
        return self.__data[row][column]

    def getArityNames(self):
        return self.__arityNameList

    def getDataNum(self):
        return self.__dataNum

    def getArities(self):
        return self.__arities

    def getTypes(self):
        return self.__types

    def displayAll(self):
        print("================================")
        print("====self.__arityNameList")
        print(self.__arityNameList)
        print("====self.__arityList")
        print(self.__arityList)
        #print("\n====self.__data")
        #print(self.__data)
        print("\n====self.__arityLength")
        print(self.__arityLength)
        print("\n====self.__dataNum")
        print(self.__dataNum)
        print("\n====self.__arities")
        for eachArity in self.__arities:
            print(eachArity)
        print("\n====self.__types")
        print(self.__types)
        print("================================")

    def printData(self):
        print("\n====self.__data")
        print(self.__data)

'''
def getRecord(row, column):
    return recordsTable[row][column]


def count(query):
    return recordsTable.count(query)

 

if __name__ == '__main__':
    global arityList
    global recordsTable
    global arityLength
    global recordsLength
    global adtree
    # import the original dataset to the record module
    #initRecord([arityList, recordsTable])
    arityList = [4, 3, 2, 5]
    recordsTable = [[1, 2, 1, 4], [2, 2, 2, 5], [1, 3, 1, 1], [4, 1, 2, 1],[2, 2, 1, 4], [4, 3, 2, 5], [3, 1, 1, 1], [1, 1, 2, 5]]

    arityLength = len(arityList)
    recordsLength = len(recordsTable)

    
    # initialise recordNums containing all numbers in the dataset
    recordNums = [num for num in range(1, recordsLength+1)]
    # build an AD-Tree with attribute list starts from the first attribute,
    # and for all the records
    adtree = ADNode(1, recordNums)
    
    # build a contingency table for the first and third attributes
    contab2 = makeContab((adtree, arityLength, arityList, recordsLength), [0,2])
    
    contab = ContingencyTable([1, 3], adtree)
    # query for [1, 1], [2, 1], [3, 1] and [4, 1], and print on screen
    for i in range(4):
        query = [i+1, 1]
        count = contab.getCount(query)
        print('Q:', query, 'C:', count)
        count2 = getCount(contab2, query)
        print('Q:', query, 'C2:', count2)
'''
