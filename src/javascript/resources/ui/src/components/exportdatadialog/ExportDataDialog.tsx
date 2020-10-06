import React from "react";
// @ts-ignore
import {Button, Textarea, Dialog, Select, TagInput} from "metro4-react";
import axios from "axios";
// @ts-ignore
import {Notific8} from 'notific8';
import "notific8/src/sass/notific8.scss";

type ExportDataDialogProps = {
    // using `interface` is also ok
    onClose: any;
};

type ExportDataDialogState = {
    // using `interface` is also ok
    data: string;
};

class ExportDataDialog extends React.Component<ExportDataDialogProps, ExportDataDialogState> {

    constructor(props: ExportDataDialogProps) {
        super(props);

        this.state = {
            data: ""
        }
    }

    componentDidMount() {
        this.getExportData();
    }

    getExportData() {
        let sState = this;
        axios.get("/api/v1/datas", {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                sState.setState({data: JSON.stringify(response.data["ExportDataResponse"])})
            }
        }).catch(function (response) {
            let message = "Error getting data";
            // @ts-ignore
            Notific8.create(message, {themeColor: 'ruby', life: 4000}).then((notification) => {
                // open the notification
                notification.open();
            });
        });
    }

    render() {
        return (
            <Dialog cls="success" open={true} modal={true} overlayAlpha={0.5} overlayColor={"#000000"}
                    onClose={this.props.onClose()} title="Export Data"
                    clsActions={"form-group d-flex flex-justify-end"} >
                <div className="form-group textareadialog">
                    <label>Database Data</label>
                    <Textarea autosize={true} value={this.state.data}/>
                </div>
            </Dialog>
        );
    }
}

export default ExportDataDialog;