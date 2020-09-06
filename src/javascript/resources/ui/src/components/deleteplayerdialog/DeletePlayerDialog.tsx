import React from "react";
// @ts-ignore
import {Button, Dialog} from "metro4-react";

type DeletePlayerDialogProps = {
    playerName: string;
    onClose: any;
    onDelete: any;
}

class DeletePlayerDialog extends React.Component<DeletePlayerDialogProps, any> {

    static defaultProps = {
        playerName: "",
        onClose: () => {},
        onDelete: () => {}
    }

    render() {
        return (<Dialog cls="alert" open={true} modal={true} overlayAlpha={0.5} overlayColor={"#000000"}
                        title="Delete Player"
                        clsActions={"form-group d-flex flex-justify-end"} onClose={this.props.onClose()}>
            <form id={"editUserForm"}>
                <div className="form-group">
                    <label>Player to delete</label>
                    <input type="text" value={this.props.playerName}/>
                    <div className="row" style={{}}>
                        <div className="cell-6">
                            <Button id={"subButton"} cls={"button secondary form-control mb-4"} title={"Cancel"}
                                    onClick={this.props.onClose()}/>
                        </div>
                        <div className="cell-6">
                            <Button id={"delButton"} cls={"button alert form-control mb-4"} title={"Delete User"}
                                    onClick={(e: any) => {
                                        console.log("dell call 2")
                                        e.preventDefault();
                                        this.props.onDelete();
                                    }}/>
                        </div>
                    </div>
                </div>

            </form>
        </Dialog>);
    }
}

export default DeletePlayerDialog;