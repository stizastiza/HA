--- module (NICHT AENDERN!)
module LascaBot where
--- imports (NICHT AENDERN!)
import Util
import Data.Char
import System.Environment
--- external signatures (NICHT AENDERN!)
getMove   :: String -> String
listMoves :: String -> String

-- *==========================================* --
-- |    HIER BEGINNT EURE IMPLEMENTIERUNG!    | --
-- *==========================================* --

--- types/structures (TODO)

type Player      = Color
data Color       = White | Black deriving (Show, Eq)
data Role        = Soldier | Officer deriving (Show, Eq)
data Piece       = Piece Color Role deriving (Show, Eq)
data Direction   = UL | UR | LL | LR deriving (Show, Eq)
    
--- logic (TODO)

getMove s = if (listMoves s) == []
                    then ""
                    else fst(splitAt 5 (cutOffBracket (listMoves s)))

listMoves s = if (length s > 48)
               then   "[" ++ cutOffLastComma (getMoveAsString (listComboMoves s) ((length (listComboMoves s))-1)) ++ "]"
               else   listNormalMoves s
------------------------------------------------------------------------------------------------------------------------- 

listNormalMoves:: String -> String
listNormalMoves s = if (readPlayer s == White)
               then   "[" ++ cutOffLastComma (getMoveAsString (getMovesW s) ((length (getMovesW s))-1)) ++ "]"
               else    "[" ++ cutOffLastComma (getMoveAsString (getMovesB s) ((length (getMovesB s))-1)) ++ "]"         

listComboMoves::  String -> [Maybe (Int, Int)]
listComboMoves s = if ((checkRoleOfPiece((game1 s)!!(getPosition(getLastMove s)))) == Just (Soldier)) 
                        then listComboMovesS s (getPosition(getLastMove s))
                        else comboMoveO (getDirection(getLastMove s)) (getPosition(getLastMove s)) (game1 s)    
               
listComboMovesS:: String -> Int -> [Maybe (Int,Int)]
listComboMovesS s i = if (readPlayer s == White)
               then   comboMoveWS i (game1 s)
               else   comboMoveBS i (game1 s)

--------ComboMoves---------

--- parses last position
getLastMove :: String -> Maybe (Int, Int)
getLastMove s = getStringAsMove (snd(splitAt 49 s))

getPosition :: Maybe (Int, Int) -> Int
getPosition  (Just (x, y)) = y 

getDirection :: Maybe(Int, Int) -> Direction
getDirection (Just (x, y)) = case (y-x) of
                                 6 -> UR  
                                 8 -> UL
                                 -8 -> LR
                                 -6 -> LL 

comboMoveWS :: Int -> [Maybe Piece] -> [Maybe (Int, Int)]
comboMoveWS  i g =  checkWSJumps i g

comboMoveBS :: Int -> [Maybe Piece] -> [Maybe (Int, Int)]
comboMoveBS  i g =  checkBSJumps i g 

comboMoveO :: Direction -> Int -> [Maybe Piece] -> [Maybe (Int, Int)]
comboMoveO d i g = case d of
                        UR -> [checkOJumpUL i g] ++ [checkOJumpLL i g] ++ [checkOJumpLR i g]
                        UL -> [checkOJumpUR i g] ++ [checkOJumpLL i g] ++ [checkOJumpLR i g]
                        LL -> [checkOJumpUR i g] ++ [checkOJumpUL i g] ++ [checkOJumpLR i g]
                        LR -> [checkOJumpUR i g] ++ [checkOJumpLL i g] ++ [checkOJumpUL i g]

-------Final Moves----
----all Moves for Black
getMovesB :: String -> [Maybe (Int, Int)]
getMovesB s = if (filterNothing(allJumpsBS (filtered s (Just Black) Soldier) (game1 s) 24 ) ++ filterNothing(allJumpsO (filtered s (Just Black) Officer) (game1 s) 24 ) == [])
                    then filterNothing(allStepsBS (filtered s (Just Black) Soldier) (game1 s) 24 ) ++ filterNothing(allStepsO (filtered s (Just Black) Officer) (game1 s) 24 )
                    else filterNothing(allJumpsBS (filtered s (Just Black) Soldier) (game1 s) 24 ) ++ filterNothing(allJumpsO (filtered s (Just Black) Officer) (game1 s) 24 )
----all Moves for White
getMovesW :: String -> [Maybe (Int, Int)]
getMovesW s = if (filterNothing(allJumpsWS (filtered s (Just White) Soldier) (game1 s) 24 ) ++ filterNothing(allJumpsO (filtered s (Just White) Officer) (game1 s) 24 ) == [])
                    then filterNothing(allStepsWS (filtered s (Just White) Soldier) (game1 s) 24 ) ++ filterNothing(allStepsO (filtered s (Just White) Officer) (game1 s) 24 )
                    else filterNothing(allJumpsWS (filtered s (Just White) Soldier) (game1 s) 24 ) ++ filterNothing(allJumpsO (filtered s (Just White) Officer) (game1 s) 24 )

---------list Moves for Black Soldier---------------------------------------
allJumpsBS :: [Maybe Piece] -> [Maybe Piece] -> Int -> [Maybe (Int, Int)]
allJumpsBS p x 0 = if p!!0 /= Nothing
                                then checkBSJumps 0 x  
                                else [Nothing]
allJumpsBS p x i = if p!!i /= Nothing
                                then checkBSJumps i x ++ allJumpsBS p x (i-1) 
                                else [Nothing] ++ allJumpsBS p x (i-1)

allStepsBS :: [Maybe Piece] -> [Maybe Piece] -> Int -> [Maybe (Int, Int)]
allStepsBS p x 0 = if p!!0 /= Nothing
                                then checkBSSteps 0 x  
                                else [Nothing]
allStepsBS p x i = if p!!i /= Nothing
                                then checkBSSteps i x ++ allStepsBS p x (i-1) 
                                else [Nothing] ++  allStepsBS p x (i-1)
                                                                
---------list Moves for White Soldier---------------------------------------

allJumpsWS :: [Maybe Piece] -> [Maybe Piece] -> Int -> [Maybe (Int, Int)]
allJumpsWS p x 0 = if p!!0 /= Nothing
                                then checkWSJumps 0 x  
                                else [Nothing]
allJumpsWS p x i = if p!!i /= Nothing
                                then checkWSJumps i x ++ allJumpsWS p x (i-1) 
                                else [Nothing] ++ allJumpsWS p x (i-1)

allStepsWS :: [Maybe Piece] -> [Maybe Piece] -> Int -> [Maybe (Int, Int)]
allStepsWS p x 0 = if p!!0 /= Nothing
                                then checkWSSteps 0 x  
                                else [Nothing]
allStepsWS p x i = if p!!i /= Nothing
                                then checkWSSteps i x ++ allStepsWS p x (i-1) 
                                else [Nothing] ++  allStepsWS p x (i-1)

---------list Moves for Officer---------------------------------

allJumpsO :: [Maybe Piece] -> [Maybe Piece] -> Int -> [Maybe (Int, Int)]
allJumpsO p x 0 = if p!!0 /= Nothing
                                then checkOJumps 0 x  
                                else [Nothing]
allJumpsO p x i = if p!!i /= Nothing
                                then checkOJumps i x ++ allJumpsO p x (i-1) 
                                else [Nothing] ++  allJumpsO p x (i-1)

allStepsO :: [Maybe Piece] -> [Maybe Piece] -> Int -> [Maybe (Int, Int)]
allStepsO p x 0 = if p!!0 /= Nothing
                                then checkOSteps 0 x  
                                else [Nothing]
allStepsO p x i = if p!!i /= Nothing
                                then checkOSteps i x ++ allStepsO p x (i-1) 
                                else [Nothing] ++  allStepsO p x (i-1)
                          
-----------CHECK JUMP MOVES----------------------------------------------------

---gets Position and checks all possible Jump Moves for White Soldier, returns List of Ints representing Pos to move to
checkWSJumps:: Int -> [Maybe Piece] -> [Maybe (Int,Int)]
checkWSJumps i x = [checkWSJumpL i x] ++ [checkWSJumpR i x]

--checks White Soldier Jump Left 
checkWSJumpL::  Int -> [Maybe Piece] -> Maybe (Int, Int)
checkWSJumpL  0 _ = Nothing
checkWSJumpL  1 _ = Nothing
checkWSJumpL  2 _ = Nothing
checkWSJumpL  3 _ = Nothing
checkWSJumpL  4 _ = Nothing
checkWSJumpL  5 _ = Nothing
checkWSJumpL  6 _ = Nothing
checkWSJumpL  7 _ = Nothing
checkWSJumpL 11 _ = Nothing
checkWSJumpL 14 _ = Nothing
checkWSJumpL 18 _ = Nothing
checkWSJumpL 21 _ = Nothing
checkWSJumpL i  x = if (not (isEmpty (x!!(i-4))) && (hasColor (x!!(i-4)) (Just Black)) && (isEmpty (x!!(i-8))))
                           then Just (i, (i-8))
                           else Nothing

--checks White Soldier Jump Right 
checkWSJumpR::  Int -> [Maybe Piece] -> Maybe (Int, Int)
checkWSJumpR  0 _ = Nothing
checkWSJumpR  1 _ = Nothing
checkWSJumpR  2 _ = Nothing
checkWSJumpR  3 _ = Nothing
checkWSJumpR  4 _ = Nothing
checkWSJumpR  5 _ = Nothing
checkWSJumpR  6 _ = Nothing
checkWSJumpR 10 _ = Nothing
checkWSJumpR 13 _ = Nothing
checkWSJumpR 17 _ = Nothing
checkWSJumpR 20 _ = Nothing
checkWSJumpR 24 _ = Nothing
checkWSJumpR i  x = if (not (isEmpty (x!!(i-3))) && (hasColor (x!!(i-3)) (Just Black)) && (isEmpty (x!!(i-6))))
                           then Just (i, (i-6))
                           else Nothing
---------------------------------------------
---gets Position and checks all possible Jump Moves for Black Soldier, returns List of Ints representing Pos to move to
checkBSJumps:: Int -> [Maybe Piece] -> [Maybe (Int,Int)]
checkBSJumps i x = [checkBSJumpL i x] ++ [checkBSJumpR i x]

--checks Black Soldier Jump Left 
checkBSJumpL::  Int -> [Maybe Piece] -> Maybe (Int, Int)
checkBSJumpL  0 _ = Nothing
checkBSJumpL  4 _ = Nothing
checkBSJumpL  7 _ = Nothing
checkBSJumpL 11 _ = Nothing
checkBSJumpL 14 _ = Nothing
checkBSJumpL 18 _ = Nothing
checkBSJumpL 19 _ = Nothing
checkBSJumpL 20 _ = Nothing
checkBSJumpL 21 _ = Nothing
checkBSJumpL 22 _ = Nothing
checkBSJumpL 23 _ = Nothing
checkBSJumpL 24 _ = Nothing
checkBSJumpL i  x = if (not (isEmpty (x!!(i + 3))) && (hasColor (x!!(i + 3)) (Just White)) && (isEmpty (x!!(i + 6))))
                           then Just (i, (i + 6))
                           else Nothing

--checks Black Soldier Jump Right 
checkBSJumpR::  Int -> [Maybe Piece] -> Maybe (Int, Int)
checkBSJumpR  3 _ = Nothing
checkBSJumpR  6 _ = Nothing
checkBSJumpR 10 _ = Nothing
checkBSJumpR 13 _ = Nothing
checkBSJumpR 17 _ = Nothing
checkBSJumpR 18 _ = Nothing
checkBSJumpR 19 _ = Nothing
checkBSJumpR 20 _ = Nothing
checkBSJumpR 21 _ = Nothing
checkBSJumpR 22 _ = Nothing
checkBSJumpR 23 _ = Nothing
checkBSJumpR 24 _ = Nothing
checkBSJumpR i  x = if (not (isEmpty (x!!(i + 4))) && (hasColor (x!!(i + 4)) (Just White)) && (isEmpty (x!!(i + 8))))
                           then Just (i, (i + 8))
                           else Nothing
---------------------------------------------
---gets Position and checks all possible Jump Moves for Officers, returns List of Ints representing Pos to move to
checkOJumps:: Int -> [Maybe Piece] -> [Maybe (Int,Int)]
checkOJumps i x = [checkOJumpUL i x] ++ [checkOJumpUR i x] ++ [checkOJumpLL i x] ++ [checkOJumpLR i x]

--checks Officer Move Upper Left 
checkOJumpUL:: Int -> [Maybe Piece] -> Maybe (Int, Int)
checkOJumpUL 0  _ = Nothing
checkOJumpUL 1  _ = Nothing
checkOJumpUL 2  _ = Nothing
checkOJumpUL 3  _ = Nothing
checkOJumpUL 4  _ = Nothing
checkOJumpUL 5  _ = Nothing
checkOJumpUL 6  _ = Nothing
checkOJumpUL 7  _ = Nothing
checkOJumpUL 11 _ = Nothing
checkOJumpUL 14 _ = Nothing
checkOJumpUL 18 _ = Nothing
checkOJumpUL 21 _ = Nothing
checkOJumpUL i  x = if (not (isEmpty (x!!(i - 4))) && not (hasColor (x!!(i - 4)) (checkColorOfPiece (x!!(i)))) && (isEmpty (x!!(i - 8))))
                           then Just (i, (i - 8))
                           else Nothing                          
                          
--checks Officer Move Upper Right 
checkOJumpUR:: Int -> [Maybe Piece] -> Maybe (Int,Int)
checkOJumpUR  0  _  = Nothing
checkOJumpUR  1  _  = Nothing
checkOJumpUR  2  _  = Nothing
checkOJumpUR  3  _  = Nothing
checkOJumpUR  4  _  = Nothing
checkOJumpUR  5  _  = Nothing
checkOJumpUR  6  _  = Nothing
checkOJumpUR 10  _  = Nothing
checkOJumpUR 13  _  = Nothing
checkOJumpUR 17  _  = Nothing
checkOJumpUR 20  _  = Nothing
checkOJumpUR 24  _  = Nothing
checkOJumpUR i   x = if (not (isEmpty (x!!(i - 3))) && not (hasColor (x!!(i - 3)) (checkColorOfPiece (x!!(i)))) && (isEmpty (x!!(i - 6))))
                           then Just (i, (i - 6))
                           else Nothing    
                           
--checks Officer Move Lower Left 
checkOJumpLL:: Int -> [Maybe Piece] -> Maybe (Int,Int)
checkOJumpLL  0  _ = Nothing
checkOJumpLL  4  _ = Nothing
checkOJumpLL  7  _ = Nothing
checkOJumpLL 11  _ = Nothing
checkOJumpLL 14  _ = Nothing
checkOJumpLL 18  _ = Nothing
checkOJumpLL 19  _ = Nothing
checkOJumpLL 20  _ = Nothing
checkOJumpLL 21  _ = Nothing
checkOJumpLL 22  _ = Nothing
checkOJumpLL 23  _ = Nothing
checkOJumpLL 24  _ = Nothing
checkOJumpLL i  x = if (not (isEmpty (x!!(i + 3))) && not (hasColor (x!!(i + 3)) (checkColorOfPiece (x!!(i)))) && (isEmpty (x!!(i + 6))))
                           then Just (i, (i + 6))
                           else Nothing  

--checks Officer Move Lower Right 
checkOJumpLR:: Int -> [Maybe Piece] -> Maybe (Int, Int)
checkOJumpLR  3 _ = Nothing
checkOJumpLR  6 _ = Nothing
checkOJumpLR 10 _ = Nothing
checkOJumpLR 13 _ = Nothing
checkOJumpLR 17 _ = Nothing
checkOJumpLR 18 _ = Nothing
checkOJumpLR 19 _ = Nothing
checkOJumpLR 20 _ = Nothing
checkOJumpLR 21 _ = Nothing
checkOJumpLR 22 _ = Nothing
checkOJumpLR 23 _ = Nothing
checkOJumpLR 24 _ = Nothing
checkOJumpLR i  x = if (not (isEmpty (x!!(i + 4))) && not (hasColor (x!!(i + 4)) (checkColorOfPiece (x!!(i)))) && (isEmpty (x!!(i + 8))))
                           then Just (i, (i + 8))
                           else Nothing  

-----------CHECK SINGLE MOVES----------------------------------------------------
---gets Position and checks all possible Moves for White Soldier, returns List of Ints representing Pos to move to
checkWSSteps:: Int -> [Maybe Piece] -> [Maybe (Int,Int)]
checkWSSteps i x = [checkWSStepL i x] ++ [checkWSStepR i x]

--checks White Soldier Step Left 
checkWSStepL::  Int -> [Maybe Piece] -> Maybe (Int, Int)
checkWSStepL  0 _ = Nothing
checkWSStepL  1 _ = Nothing
checkWSStepL  2 _ = Nothing
checkWSStepL  3 _ = Nothing
checkWSStepL  7 _ = Nothing
checkWSStepL 14 _ = Nothing
checkWSStepL 21 _ = Nothing
checkWSStepL i  x = if isEmpty (x!!(i - 4))
                           then Just (i, (i - 4))
                           else Nothing

--checks White Soldier Step Right 
checkWSStepR::  Int -> [Maybe Piece] ->  Maybe (Int, Int)
checkWSStepR  0 _ = Nothing
checkWSStepR  1 _ = Nothing
checkWSStepR  2 _ = Nothing
checkWSStepR  3 _ = Nothing
checkWSStepR 10 _ = Nothing
checkWSStepR 17 _ = Nothing
checkWSStepR 24 _ = Nothing
checkWSStepR i  x = if isEmpty (x!!(i - 3))
                            then Just (i, i - 3)
                            else Nothing
---------------------------------------------
---gets Position and checks all possible Moves for Black Soldier, returns List of Ints representing Pos to move to
checkBSSteps:: Int -> [Maybe Piece] -> [Maybe (Int, Int)]
checkBSSteps i x = [checkBSStepL i x] ++ [checkBSStepR i x]

--checks Black Soldier Step Left 
checkBSStepL::  Int -> [Maybe Piece] -> Maybe (Int,Int)
checkBSStepL  0 _ = Nothing
checkBSStepL  7 _ = Nothing
checkBSStepL 14 _ = Nothing
checkBSStepL 21 _ = Nothing
checkBSStepL 22 _ = Nothing
checkBSStepL 23 _ = Nothing
checkBSStepL 24 _ = Nothing
checkBSStepL i  x = if isEmpty (x!!(i+3))
                        then Just (i, i + 3)
                        else Nothing
--checks Black Soldier Step Right 
checkBSStepR::  Int -> [Maybe Piece] -> Maybe (Int, Int)
checkBSStepR  3 _ = Nothing
checkBSStepR 10 _ = Nothing
checkBSStepR 17 _ = Nothing
checkBSStepR 21 _ = Nothing
checkBSStepR 22 _ = Nothing
checkBSStepR 23 _ = Nothing
checkBSStepR 24 _ = Nothing
checkBSStepR i  x = if isEmpty (x!!(i+4))
                       then Just (i, i + 4)
                       else Nothing
----------------------------------------------
---gets Position and checks all possible Moves for Officer, returns List of Ints representing Pos to move to
checkOSteps:: Int -> [Maybe Piece] -> [Maybe (Int,Int)]
checkOSteps i x = [checkOStepUL i x] ++ [checkOStepUR i x] ++ [checkOStepLL i x] ++ [checkOStepLR i x]

--checks Officer Move Upper Left 
checkOStepUL:: Int -> [Maybe Piece] -> Maybe (Int, Int)
checkOStepUL 0  _ = Nothing
checkOStepUL 1  _ = Nothing
checkOStepUL 2  _ = Nothing
checkOStepUL 3  _ = Nothing
checkOStepUL 7  _ = Nothing
checkOStepUL 14 _ = Nothing
checkOStepUL 21 _ = Nothing
checkOStepUL i  x = if isEmpty (x!!(i-4)) 
                          then Just (i, i - 4)
                          else Nothing
--checks Officer Move Upper Right 
checkOStepUR:: Int -> [Maybe Piece] -> Maybe (Int,Int)
checkOStepUR 0  _  = Nothing
checkOStepUR 1  _  = Nothing
checkOStepUR 2  _  = Nothing
checkOStepUR 3  _  = Nothing
checkOStepUR 10 _  = Nothing
checkOStepUR 17 _  = Nothing
checkOStepUR 24 _  = Nothing
checkOStepUR i  x = if isEmpty (x!!(i-3)) 
                          then Just (i, i - 3)
                          else Nothing
                          
--checks Officer Move Lower Left 
checkOStepLL:: Int -> [Maybe Piece] -> Maybe (Int,Int)
checkOStepLL 24  _ = Nothing
checkOStepLL 23  _ = Nothing
checkOStepLL 22  _ = Nothing
checkOStepLL 21  _ = Nothing
checkOStepLL 7   _ = Nothing
checkOStepLL 14  _ = Nothing
checkOStepLL  0  _ = Nothing
checkOStepLL i  x = if isEmpty (x!!(i + 3)) 
                          then Just (i, i + 3)
                          else Nothing

--checks Officer Move Lower Right 
checkOStepLR:: Int -> [Maybe Piece] -> Maybe (Int, Int)
checkOStepLR 24 _ = Nothing
checkOStepLR 23 _ = Nothing
checkOStepLR 22 _ = Nothing
checkOStepLR 21 _ = Nothing
checkOStepLR 10 _ = Nothing
checkOStepLR 17 _ = Nothing
checkOStepLR  3 _ = Nothing
checkOStepLR  i x = if isEmpty (x!!(i + 4)) 
                          then Just (i, i + 4)
                          else Nothing

---------------------------------------
--check If Position Is Empty
isEmpty:: Maybe Piece -> Bool
isEmpty   p = if p == Nothing 
                          then True
                          else False
----------------------------------------------------------------------
-- Gets List of allPieces of one Color and Returns List for only OneColor/OneRole
filtered :: String -> Maybe Color -> Role -> [Maybe Piece]
filtered s (Just c) r = filterByRole (filterByColor (readAllPieces s 24) (Just c)) r 

game1:: String -> [Maybe Piece]
game1 s = readAllPieces s 24

filterByRole:: [Maybe Piece] -> Role -> [Maybe Piece]
filterByRole [] _ = []
filterByRole (x:xs) r = if (hasRole x r)
                                        then    x:filterByRole xs r
                                        else    Nothing:filterByRole xs r

hasRole:: Maybe Piece ->  Role -> Bool
hasRole Nothing r2 = False
hasRole  (Just (Piece _ r1)) r2  = (r1 == r2)

checkRoleOfPiece:: Maybe Piece -> Maybe Role
checkRoleOfPiece   Nothing = Nothing
checkRoleOfPiece   (Just (Piece _ r)) = Just r
---------------------------------------------------------------------
--Filters List of AllPieces By Color and Returns List with only one Color
filterByColor:: [Maybe Piece] -> Maybe Color -> [Maybe Piece]
filterByColor [] _ = []
filterByColor (x:xs) c = if (hasColor x c)
                                       then     x:filterByColor xs c
                                       else     Nothing:filterByColor xs c

hasColor:: Maybe Piece -> Maybe Color -> Bool
hasColor x c = (checkColorOfPiece(x) == c)

checkColorOfPiece:: Maybe Piece -> Maybe Color
checkColorOfPiece   Nothing = Nothing
checkColorOfPiece   (Just (Piece c _)) = Just c

                
--- input (TODO)
 
----determines Player
readPlayer :: String -> Player
readPlayer s = if s!!47 == 'w'
              then White
              else Black

---create Array with all Pieces
readAllPieces :: String -> Int -> [Maybe Piece] 
readAllPieces s 0 = [(readPiece((parsePosition s)!!0))]
readAllPieces s i = readAllPieces s (i-1) ++ [(readPiece((parsePosition s)!!i))]

---parses FEN-String and returns seperate String for each Coordinate            
parsePosition :: String -> [String]      
parsePosition s = splitOn "," (replace (s)) 

--replace '/' with ',' in FEN-String
replace:: String -> String    
replace [] = []
replace (x:xs) = 
     if x == '/'
     then ',' : replace xs
     else x : replace xs   
     
----creates Piece from String
readPiece :: String -> Maybe Piece
readPiece ""  = Nothing
readPiece (' ' : xs) = Nothing
readPiece ('w' : xs) = Just (Piece White Soldier)
readPiece ('W' : xs) = Just (Piece White Officer)
readPiece ('b' : xs) = Just (Piece Black Soldier) 
readPiece ('B' : xs) = Just (Piece Black Officer) 

--- output (TODO)

----------------- get MoveString
cutOffLastComma :: String -> String
cutOffLastComma [] = []
cutOffLastComma  s = if [last(s)] == "," 
                        then init (s)
                        else s
                        
cutOffBracket :: String -> String 
cutOffBracket s = tail s

getMoveAsString :: [Maybe (Int,Int)] -> Int -> String
getMoveAsString   x 0 = moveToString (x!!0)
getMoveAsString   x i = moveToString (x!!i) ++ getMoveAsString x (i-1)

getStringAsMove :: String -> Maybe (Int,Int)
getStringAsMove  s = Just (stringToIndex ([s!!0] ++ [s!!1]) , stringToIndex ([s!!3] ++ [s!!4]))

moveToString :: Maybe (Int, Int) -> String
moveToString Nothing = ""
moveToString (Just (x, y))  = indexToString x ++ "-" ++ indexToString y ++ ","

indexToString:: Int -> String
indexToString 0  = "a7"
indexToString 1  = "c7"
indexToString 2  = "e7"
indexToString 3  = "g7"
indexToString 4  = "b6"
indexToString 5  = "d6"
indexToString 6  = "f6"
indexToString 7  = "a5"
indexToString 8  = "c5"
indexToString 9  = "e5"
indexToString 10 = "g5"
indexToString 11 = "b4"
indexToString 12 = "d4"
indexToString 13 = "f4"
indexToString 14 = "a3"
indexToString 15 = "c3"
indexToString 16 = "e3"
indexToString 17 = "g3"
indexToString 18 = "b2"
indexToString 19 = "d2"
indexToString 20 = "f2"
indexToString 21 = "a1"
indexToString 22 = "c1"
indexToString 23 = "e1"
indexToString 24 = "g1"

stringToIndex:: String -> Int
stringToIndex  "a7" = 0
stringToIndex  "c7" = 1
stringToIndex  "e7" = 2
stringToIndex  "g7" = 3
stringToIndex  "b6" = 4
stringToIndex  "d6" = 5
stringToIndex  "f6" = 6
stringToIndex  "a5" = 7
stringToIndex  "c5" = 8
stringToIndex  "e5" = 9
stringToIndex  "g5" = 10
stringToIndex  "b4" = 11
stringToIndex  "d4" = 12
stringToIndex  "f4" = 13
stringToIndex  "a3" = 14
stringToIndex  "c3" = 15
stringToIndex  "e3" = 16
stringToIndex  "g3" = 17
stringToIndex  "b2" = 18
stringToIndex  "d2" = 19
stringToIndex  "f2" = 20
stringToIndex  "a1" = 21
stringToIndex  "c1" = 22
stringToIndex  "e1" = 23
stringToIndex  "g1" = 24
-------------------------------------------------------------------------
filterNothing:: [Maybe (Int,Int)] -> [Maybe (Int,Int)]
filterNothing [] = []
filterNothing (x:xs) = if (x /= Nothing)
                                        then    x      :filterNothing xs 
                                        else    filterNothing xs  
-------------------------------------------------------------------------