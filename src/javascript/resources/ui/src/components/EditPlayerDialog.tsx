import React, {FormEvent} from "react";
import axios from "axios";
// @ts-ignore
import {Notific8} from 'notific8';
import "../../node_modules/notific8/src/sass/notific8.scss";
// @ts-ignore
import {Button, Checkbox, Dialog, Select, TagInput} from "metro4-react";

type EditPlayerDialogProps = {
    // using `interface` is also ok
    onClose: any;
    data?: Array<JSX.Element>;
};
type EditPlayerDialogState = {
    id?: number;
    tankPref?: number;
    supportPref?: number;
    dpsPref?: number;
    tankSr?: number;
    supportSr?: number;
    dpsSr?: number;
    username?: string;
    owNames?: Array<string>
};

class EditPlayerDialog extends React.Component<EditPlayerDialogProps, EditPlayerDialogState> {
    constructor(props: EditPlayerDialogProps) {
        super(props);

        this.state = {
            id: -1,
            tankPref: -1,
            supportPref: -1,
            dpsPref: -1,
            tankSr: undefined,
            supportSr: undefined,
            dpsSr: undefined,
            username: "",
            owNames: []
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.tankCheckBox = this.tankCheckBox.bind(this);
        this.supportCheckBox = this.supportCheckBox.bind(this);
        this.dpsBox = this.dpsBox.bind(this);
        this.handleTankSrChange = this.handleTankSrChange.bind(this);
        this.handleSupportSrChange = this.handleSupportSrChange.bind(this);
        this.handleDpsSrChange = this.handleDpsSrChange.bind(this);
        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handleOwNamesChange = this.handleOwNamesChange.bind(this);
        this.onSelectChange = this.onSelectChange.bind(this);
    }

    componentDidMount() {
        this.setDisabled(true);
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        await this.login(e);
    }

    resetState() {
        this.setState({
            id: -1,
            tankPref: -1,
            supportPref: -1,
            dpsPref: -1,
            tankSr: undefined,
            supportSr: undefined,
            dpsSr: undefined,
            username: "",
            owNames: []
        });
    }

    async login(e: FormEvent) {
        let message = "Invalid request";
        let color = "ruby";
        try {
            const response = await axios.post("/api/v1/users/"+this.state.id, {
                username: this.state.username,
                overwatchNames: this.state.owNames,
                tankSr: this.state.tankSr,
                tankPreference: this.state.tankPref,
                dpsSr: this.state.dpsSr,
                dpsPreference: this.state.dpsPref,
                supportSr: this.state.supportSr,
                supportPreference: this.state.supportPref,
            }, {
                responseType: "json",
            });
            if (response.status === 200) {
                message = "User updated successfully";
                color = "lime";
            }
            // @ts-ignore
            this.resetState();
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
    }

    handleUsernameChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({username: event.currentTarget.value});
        }
    }

    handleOwNamesChange(tags: Array<string>) {
        this.setState({owNames: tags});
    }

    handleTankSrChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({tankSr: Number.parseInt(event.currentTarget.value)});
        }
    }

    handleSupportSrChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({supportSr: Number.parseInt(event.currentTarget.value)});
        }
    }

    handleDpsSrChange(event: React.FormEvent<HTMLInputElement>) {
        if (event.target !== null) {
            this.setState({dpsSr: Number.parseInt(event.currentTarget.value)});
        }
    }

    tankCheckBox(val: number) {
        this.setState({tankPref: val})
    }

    supportCheckBox(val: number) {
        this.setState({supportPref: val})
    }

    dpsBox(val: number) {
        this.setState({dpsPref: val})
    }

    submitForm() {
        // @ts-ignore
        document.getElementById("addUserForm").submit();
    }

    setDisabled(bool: Boolean) {
        let frm = document.getElementById("editUserForm");
        let sbbtn = document.getElementById("subButton");
        if (bool) {
            // @ts-ignore
            frm.classList.add("disabled");
            // @ts-ignore
            sbbtn.classList.add("disabled");
        } else {
            // @ts-ignore
            frm.classList.remove("disabled");
            // @ts-ignore
            sbbtn.classList.remove("disabled");
        }
    }

    getUserInfo(id: number) {
        let sState = this;
        axios.get("/api/v1/users/" + id, {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                sState.setState({
                    id: id,
                    tankPref: Number.parseInt(response.data["api"]["userInfo"]["tankPreference"]),
                    supportPref: Number.parseInt(response.data["api"]["userInfo"]["supportPreference"]),
                    dpsPref: Number.parseInt(response.data["api"]["userInfo"]["dpsPreference"]),
                    tankSr: Number.parseInt(response.data["api"]["userInfo"]["tankSr"]),
                    supportSr: Number.parseInt(response.data["api"]["userInfo"]["supportSr"]),
                    dpsSr: Number.parseInt(response.data["api"]["userInfo"]["dpsSr"]),
                    username: response.data["api"]["userInfo"]["name"],
                    owNames: response.data["api"]["owNames"]
                });
                sState.setDisabled(false);
            }
        }).catch(function (response) {
            let message = "Error getting users";
            // @ts-ignore
            Notific8.create(message, {themeColor: 'ruby', life: 4000}).then((notification) => {
                // open the notification
                notification.open();
            });
        });
    }

    onSelectChange() {
        let selected = undefined;
        // @ts-ignore
        for (let option of document.getElementById("editSelect").options) {
            if (option.selected) {
                selected = option.value;
                break;
            }
        }
        if(selected !== undefined) {
            this.getUserInfo(selected);
        }
    }

    render() {
        return (<Dialog cls="primary" open={true} modal={true} overlayAlpha={0.5} overlayColor={"#000000"}
                        onClose={this.props.onClose()} title="Edit Player"
                        clsActions={"form-group d-flex flex-justify-end"}>
            <Select cls={"mb-3"} onChange={this.onSelectChange} selectId={"editSelect"}>
                {this.props.data}
            </Select>
            <form id={"editUserForm"} onSubmit={(e) => this.handleSubmit(e)}>
                <div className="form-group">
                    <label>Discord Username</label>
                    <input type="text" placeholder="Enter discord username" onChange={this.handleUsernameChange}
                           value={this.state.username}/>
                </div>
                <div className="form-group">
                    <label>Overwatch Usernames</label>
                    <TagInput onChange={this.handleOwNamesChange} tags={this.state.owNames}/>
                    <small className="text-muted">Please do not share another users SR unless they have given you
                        permission.</small>
                </div>
                <div className="form-group">
                    <label>Tank SR</label>
                    <input type="number" placeholder="2500" onChange={this.handleTankSrChange}
                           value={this.state.tankSr}/>
                </div>
                <div className="form-group">
                    <Checkbox caption={"Primary Tank"} onChange={() => this.tankCheckBox(2)}
                              checked={this.state.tankPref === 2}/>
                    <Checkbox caption={"Secondary Tank"} onChange={() => this.tankCheckBox(1)}
                              checked={this.state.tankPref === 1}/>
                    <Checkbox caption={"No Tank"} onChange={() => this.tankCheckBox(0)}
                              checked={this.state.tankPref === 0}/>
                </div>
                <div className="form-group">
                    <label>DPS SR</label>
                    <input type="number" placeholder="2500" onChange={this.handleDpsSrChange} value={this.state.dpsSr}/>
                </div>
                <div className="form-group">
                    <Checkbox caption={"Primary DPS"} onChange={() => this.dpsBox(2)}
                              checked={this.state.dpsPref === 2}/>
                    <Checkbox caption={"Secondary DPS"} onChange={() => this.dpsBox(1)}
                              checked={this.state.dpsPref === 1}/>
                    <Checkbox caption={"No DPS"} onChange={() => this.dpsBox(0)} checked={this.state.dpsPref === 0}/>
                </div>
                <div className="form-group">
                    <label>Support SR</label>
                    <input type="number" placeholder="2500" onChange={this.handleSupportSrChange}
                           value={this.state.supportSr}/>
                </div>
                <div className="form-group">
                    <Checkbox caption={"Primary Support"} onChange={() => this.supportCheckBox(2)}
                              checked={this.state.supportPref === 2}/>
                    <Checkbox caption={"Secondary Support"} onChange={() => this.supportCheckBox(1)}
                              checked={this.state.supportPref === 1}/>
                    <Checkbox caption={"No Support"} onChange={() => this.supportCheckBox(0)}
                              checked={this.state.supportPref === 0}/>
                </div>
                <div className="form-group">
                    <Button id={"subButton"} cls={"button primary form-control mb-4"} title={"Submit User Changes"}
                            type={"submit"}/>
                </div>
            </form>
        </Dialog>);
    }
}

export default EditPlayerDialog;
