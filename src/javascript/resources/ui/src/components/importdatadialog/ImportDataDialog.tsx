import React, {FormEvent} from "react";
// @ts-ignore
import {Button, Textarea, Dialog, Select, TagInput} from "metro4-react";
import axios from "axios";
// @ts-ignore
import {Notific8} from 'notific8';
import "notific8/src/sass/notific8.scss";

type ImportDataDialogProps = {
    // using `interface` is also ok
    onClose: any;
    onUpdate: any;
};

type ImportDataDialogState = {
    // using `interface` is also ok
    data: string;
};

class ImportDataDialog extends React.Component<ImportDataDialogProps, ImportDataDialogState> {

    constructor(props: ImportDataDialogProps) {
        super(props);

        this.state = {
            data: ""
        }

        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleDataChange = this.handleDataChange.bind(this);
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        await this.login(e);
    }

    async login(e: FormEvent) {
        let message = "Invalid request";
        let color = "ruby";
        let dataObj = JSON.parse(this.state.data)
        try {
            const response = await axios.post("/api/v1/datas", {
                userInfo: dataObj["userInfo"],
                userNames: dataObj["userNames"],
            }, {
                responseType: "json",
            });
            if (response.status === 200) {
                message = "User updated successfully";
                color = "lime";
            }
        } catch (e) {
            if (e.message.endsWith("406")) {
                message = "Invalid data"
            }
        }
        // @ts-ignore
        Notific8.create(message, {themeColor: color, life: 4000}).then((notification) => {
            // open the notification
            notification.open();
        });
        if (color === "lime") {
            this.props.onUpdate();
        }
    }

    handleDataChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({data: event.currentTarget.value});
        }
    }

    render() {
        return (
            <Dialog cls="alert" open={true} modal={true} overlayAlpha={0.5} overlayColor={"#000000"}
                    onClose={this.props.onClose()} title="Import Data"
                    clsActions={"form-group d-flex flex-justify-end"} >
                <form id={"importDataForm"} onSubmit={(e) => this.handleSubmit(e)}>
                    <div className="form-group textareadialog">
                        <label>Database Data</label>
                        <Textarea autosize={true} value={this.state.data} onChange={this.handleDataChange}/>
                    </div>
                    <div className="form-group">
                        <Button id={"subButton"} cls={"button success form-control mb-4"} title={"Submit Data"}
                                type={"submit"}/>
                    </div>
                </form>
            </Dialog>
        );
    }
}

export default ImportDataDialog;