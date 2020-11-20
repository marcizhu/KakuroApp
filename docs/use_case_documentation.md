# Documentation: Use Cases

## Create user

**Parameters**
- `nickname` (string)

**Preconditions**
- There is no user logged in.
- There is no user in the database using the provided nickname.

**Flow**
1. User writes the desired nickname in a text field.
2. User clicks a “Register” button.
3. User is added to the database with default values.
4. User is automatically logged in and redirected to the main dashboard.

**Exceptions**
- If the provided nickname is already registered in the database, an exception will be
thrown and the client will notify the user.

## Login

**Parameters**
- `nickname` (string)

**Preconditions**
- There is no user logged in.
- A user with the provided nickname exists in the database.

**Flow**
1. User writes his nickname in a text field.
2. User clicks a “Log in” button.
3. User is logged in and redirected to the main dashboard.

**Exceptions**
- If the provided nickname is not registered in the database, an exception will be
thrown and the client will notify the user.

## Import Kakuro

**Parameters**
- `file` (string)

**Preconditions**
- User is logged in.
- The provided path exists and contains a kakuro with the correct format.

**Flow**
1. User provides a .kak file by selecting it in a file explorer select file dialog.
2. The provided Kakuro is verified.
3. The provided Kakuro is added to the database.
4. User is redirected to the play kakuro screen.

**Exceptions**
- If the provided Kakuro is not valid, an exception will be thrown and the client will
    notify the user.

## Generate Kakuro

**Parameters**
- `rowSize` (int)
- `colSize` (int)
- `difficulty` (int)

**Flow**
1. The Kakuro generator generates a Kakuro with the provided dimensions and
difficulty.
2. User is sent to the play Kakuro screen for the generated Kakuro.

**Exceptions**

## List kakuros

**Parameters**

**Preconditions**
- User is logged in.

**Flow**
1. User presses the list Kakuro button in the main dashboard.
2. A list of all the Kakuros in the database is displayed in the Kakuro list screen.
3. For each Kakuro, the user will be able to choose between these options:
    - Export Kakuro.
    - Play Kakuro.

**Exceptions**

## Create Kakuro

**Parameters**
- `rowSize` (int)
- `colSize` (int)

**Preconditions**
- User is logged in.

**Flow**
1. User provides the size for the rows and columns of the Kakuro they aim to create.
2. User is redirected to the Kakuro creator screen. Initially, a Kakuro of the provided
    dimensions where all cells are black. In the Kakuro creator screen user will be able to
    execute a sequence of several actions:
    - Swap cell color (black -> white or white -> black). When executing this action
    the corresponding cell will be set to the default state, in case of black cells it
    would be full black (without vertical or horizontal sums) and in the case of
    white cells it would be a whie cell with no defined value.
    - Add/remove value to a white cell.
    - Add/remove horizontal and/or vertical sum value to a black cell.
3. Every time one action is performed a validator will be executed and the status of the
    Kakuro (valid or invalid) will be displayed to the user.
4. At any given time the user would be able to leave the Kakuro creator not saving the
    result.
5. If the current state of the Kakuro the user is creating is valid, the user will have the
    option to save the Kakuro, if the user decides to do so, the Kakuro will be stored in
    the database and the user will be redirected to the Kakuro list screen.

**Exceptions**

## Play Kakuro

**Parameters**
- `kakuroID` (uuid)

**Preconditions**
- User is logged in.

**Flow**
1. User selects a Kakuro from the Kakuro list.
2. The Kakruo play screen is displayed, initially the user will see the empty state of the
Kakuro.
3. At any given point the user will be able to exit and save progress (or not) for the
game that they are playing.
4. While the user is playing they will be able to perform a sequence of actions:
    - Undo movement.
    - Play movement.
    - Modify annotations.
    - Ask for help.
    - Solve Kakuro.
    - Validate Kakuro.

**Exceptions**

## Undo movement

**Parameters**

**Preconditions**
- User is logged in.
- User is the play Kakuro screen.

**Flow**
1. User presses the undo movement button.
2. The last movement done by the user is undone and it is reflected in the board.

**Exceptions**

## Play movement

**Parameters**

**Preconditions**
- User is logged in.
- User is the play Kakuro screen.

**Flow**
1. User sets a value for a white cell.
2. The value is set and reflected in the board.

**Exceptions**

## Modify annotations

**Parameters**

**Preconditions**
- User is logged in.
- User is the play Kakuro screen.

**Flow**
1. User modifies annotations for a given white cell.
2. The annotation is stored successfully in the white cell.

**Exceptions**

## Ask for help

**Parameters**

**Preconditions**
- User is logged in.
- User is the play Kakuro screen.

**Flow**
1. User presses the help button.
2. User is asked to use between the available helping options.
    - A validator that tells the user whether the placed values are correct.
    - If the solution is unique, some correct values can be placed in the board.

**Exceptions**

## Solve kakuro

**Parameters**

**Preconditions**
- User is logged in.
- User is the play Kakuro screen.

**Flow**
1. User presses the solve Kakuro button.
2. A solution for the Kakuro that is being played is displayed in the play screen.
3. Current game is finished and user does not get any points from it.

**Exceptions**

## Validate kakuro

**Parameters**

**Preconditions**
- User is logged in.
- User is the play Kakuro screen.

**Flow**
1. User presses the validate Kakuro button.
2. The Kakuro that is being played is validated.
3. A message is displayed in the play screen saying either if the current values for the
Kakuro being played are valid or not. In case that they are not the failing values are
marked as red.
4. If the Kakuro is valid and all the white cells are filled the game has been successfully
completed.

**Exceptions**

## Export Kakuro

**Parameters**
- `kakuroID` (uuid)
- `outputDirectory` (string)

**Preconditions**
- User is logged in.
- The provided directory exists.
- A Kakuro with the provided ID exists in the database.

**Flow**
1. The export Kakuro use case may be reached from two places:
    - User selects the export Kakuro option for one of the Kakuros in the Kakuro
    list. This would imply exporting the selected Kakuro unsolved.
    - User is in the play Kakuro screen and selects the export Kakuro option which
would export the current state of the board.
2. User selects the output directory by selecting it in a file explorer select directory
dialog.
3. A file is created in the form of `kakuroID`.kak in the provided output directory
containing the desired Kakuro in the standard format.

**Exceptions**
- If the output directory cannot be reached, an exception will be thrown and the client
will notify the user.

## View statistics

**Parameters**

**Preconditions**
- User is logged in.

**Flow**
1. The statistics for the current user are displayed.
2. Several types of rankings are displayed:
    - Ranking of users that have played more levels for each difficulty.
    - Ranking of users that have played more time.
2. Using a search bar, the user will be able to search specific statistics for all the users
    in the database.

**Exceptions**
