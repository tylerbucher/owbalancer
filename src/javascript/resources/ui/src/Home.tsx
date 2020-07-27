import React, {FormEvent} from "react";
import axios from "axios";
// @ts-ignore
import {Notific8} from 'notific8';
import "../node_modules/notific8/src/sass/notific8.scss";
// @ts-ignore
import {Button, Dropdown, Select, MultiAction, MultiActionItem, Dialog} from "metro4-react";
import AddPlayerDialog from "./components/AddPlayerDialog";
import EditPlayerDialog from "./components/EditPlayerDialog";

type LoginProps = {
    // using `interface` is also ok
};
type LoginState = {
    selectData?: Array<JSX.Element>;
    selectedSelectData?: Array<string>;
    table1Rows?: Array<JSX.Element>;
    table1MetaRows?: Array<JSX.Element>;
    table2Rows?: Array<JSX.Element>;
    table2MetaRows?: Array<JSX.Element>;
    showAddPlayerDialog?: boolean;
    showEditPlayerDialog?: boolean;
    editUserList?: Array<JSX.Element>;
};

class Home extends React.Component<LoginProps, LoginState> {


    constructor(props: LoginProps) {
        super(props);

        this.state = {
            selectData: new Array<JSX.Element>(),
            selectedSelectData: new Array<string>(),
            table1Rows: this.initTable(),
            table1MetaRows: new Array<JSX.Element>(),
            table2Rows: this.initTable(),
            table2MetaRows: new Array<JSX.Element>(),
            showAddPlayerDialog: false,
            showEditPlayerDialog: false,
            editUserList: new Array<JSX.Element>()
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.fetchNewUsers = this.fetchNewUsers.bind(this);
        this.openAddUserDialog = this.openAddUserDialog.bind(this);
        this.openEditUserDialog = this.openEditUserDialog.bind(this);
    }

    initTable() {
        let jsxArray = new Array<JSX.Element>();
        for (let i = 0; i < 6; i++) {
            jsxArray.push(this.buildBlankTableRow());
        }
        return jsxArray;
    }

    buildBlankTableRow() {
        return (
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
        );
    }

    buildMultiDataRow(position: String, name: String, sr: String, te: number, de: number, se: number) {
        return (
            <tr>
                <td>{position}</td>
                <td>{name}</td>
                <td>{sr}</td>
                <td><span className={te == 0 ? "mif-not fg-red" : te == 1 ? "mif-checkmark fg-cobalt" : "mif-checkmark fg-green"}/></td>
                <td><span className={de == 0 ? "mif-not fg-red" : de == 1 ? "mif-checkmark fg-cobalt" : "mif-checkmark fg-green"}/></td>
                <td><span className={se == 0 ? "mif-not fg-red" : se == 1 ? "mif-checkmark fg-cobalt" : "mif-checkmark fg-green"}/></td>
            </tr>
        );
    }

    buildKVPDataRow(key: String, value: String) {
        return (
            <tr>
                <td>{key}</td>
                <td>{value}</td>
            </tr>
        );
    }

    componentDidMount() {
        this.getUserList(false);
    }

    fetchNewUsers() {
        this.getUserList(true);
    }

    openAddUserDialog() {
        this.setState({showAddPlayerDialog: true});
    }

    openEditUserDialog() {
        this.setState({showEditPlayerDialog: true});
    }

    getUserList(notify: boolean) {
        let sState = this;
        axios.get("/api/v1/users/-1", {
            responseType: "json",
        }).then(function (response) {
            if (response.status === 200) {
                let mm = new Array<JSX.Element>();
                let editUserList = new Array<JSX.Element>();
                let index = 0;
                response.data["api"]["users"].forEach(function (value: Object) {
                    // @ts-ignore
                    let id = value["id"];
                    // @ts-ignore
                    editUserList.push(<option value={id}>{value["discordName"]}</option>)
                    // @ts-ignore
                    value["owNames"].forEach(function (value: Object) {
                        let key = Object.keys(value)[0];
                        // @ts-ignore
                        mm.push(<option value={(index++) + "#" + id}>{value}</option>);
                    });
                });
                if(notify) {
                    let message = "User list updated";
                    // @ts-ignore
                    Notific8.create(message, {themeColor: 'lime', life: 4000}).then((notification) => {
                        // open the notification
                        notification.open();
                    });
                }
                sState.setState({selectData: mm, editUserList: editUserList});
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

    setDisabled(bool: Boolean) {
        let sb = document.getElementById("sb");
        let sl = document.getElementsByClassName("select")[0];
        if (bool) {
            // @ts-ignore
            sb.classList.add("disabled");
            // @ts-ignore
            sl.classList.add("disabled");
            this.setState({
                table1Rows: this.initTable(),
                table1MetaRows: new Array<JSX.Element>(),
                table2Rows: this.initTable(),
                table2MetaRows: new Array<JSX.Element>()
            })
        } else {
            // @ts-ignore
            sb.classList.remove("disabled");
            // @ts-ignore
            sl.classList.remove("disabled");
        }
    }

    async handleSubmit(e: FormEvent) {
        e.preventDefault();

        this.setDisabled(true);
        await this.balance();
    }

    async balance() {
        let ref = this;
        let selected = new Set<number>();
        let selectedData = new Array<string>();
        // @ts-ignore
        for (let option of document.getElementsByTagName("select")[0].options) {
            if (option.selected) {
                selectedData.push(option.value);
                selected.add(Number.parseInt(option.value.split("#")[1]));
            }
        }
        try {
            const response = await axios.post("/api/v1/balance", {
                userIds: Array.from(selected),
            }, {
                responseType: "json",
            });
            if (response.status === 200) {
                let team1List = new Array<JSX.Element>();
                let team2List = new Array<JSX.Element>();

                let team1Meta = new Array<JSX.Element>();
                let team2Meta = new Array<JSX.Element>();

                response.data["api"]["userList"].forEach(function (value: Object, index: number) {
                    // @ts-ignore
                    let position = Number.parseInt(value["position"]);
                    // @ts-ignore
                    let tpf = value["user"]["tankPreference"];
                    // @ts-ignore
                    let dpf = value["user"]["dpsPreference"];
                    // @ts-ignore
                    let spf = value["user"]["supportPreference"];
                    // @ts-ignore
                    if (Number.parseInt(value["team"]) === 1) {
                        // @ts-ignore
                        team1List.push(ref.buildMultiDataRow(ref.getPositionForId(position), value["user"]["name"], ref.getPositionSr(position, value["user"]), tpf, dpf, spf));
                    } else {
                        // @ts-ignore
                        team2List.push(ref.buildMultiDataRow(ref.getPositionForId(position), value["user"]["name"], ref.getPositionSr(position, value["user"]), tpf, dpf, spf));
                    }
                });
                while (team1List.length < 6) {
                    team1List.push(ref.buildBlankTableRow());
                }
                while (team2List.length < 6) {
                    team2List.push(ref.buildBlankTableRow());
                }
                // stats team 1
                let av1Diff = response.data["api"]["balancerMeta"]["team1AverageSr"] - response.data["api"]["balancerMeta"]["team2AverageSr"];
                let totalAv1Diff = response.data["api"]["balancerMeta"]["team1TotalAverageSr"] - response.data["api"]["balancerMeta"]["team2TotalAverageSr"];
                team1Meta.push(ref.buildKVPDataRow("Balance Score", response.data["api"]["balancerMeta"]["balanceScore"]))
                team1Meta.push(ref.buildKVPDataRow("Average SR", response.data["api"]["balancerMeta"]["team1AverageSr"] + " (Δ " + av1Diff + ")"))
                team1Meta.push(ref.buildKVPDataRow("└─ Total SR", response.data["api"]["balancerMeta"]["team1TotalSr"]))
                team1Meta.push(ref.buildKVPDataRow("Average SR (All roles)", response.data["api"]["balancerMeta"]["team1TotalAverageSr"] + " (Δ " + totalAv1Diff + ")"))
                team1Meta.push(ref.buildKVPDataRow("└─ Total SR (All roles)", response.data["api"]["balancerMeta"]["team1TotalSrDistribution"]))
                team1Meta.push(ref.buildKVPDataRow("Adaptability (How well the team can adapt to playing different roles)", response.data["api"]["balancerMeta"]["team1Adaptability"] + "%"))
                team1Meta.push(ref.buildKVPDataRow("├─ Tank Adaptability", response.data["api"]["balancerMeta"]["team1TankAdaptability"] + "%"))
                team1Meta.push(ref.buildKVPDataRow("├─ DPS Adaptability", response.data["api"]["balancerMeta"]["team1DpsAdaptability"] + "%"))
                team1Meta.push(ref.buildKVPDataRow("└─ Support Adaptability", response.data["api"]["balancerMeta"]["team1SupportAdaptability"] + "%"))
                // stats team 2
                let av2Diff = response.data["api"]["balancerMeta"]["team2AverageSr"] - response.data["api"]["balancerMeta"]["team1AverageSr"];
                let totalAv2Diff = response.data["api"]["balancerMeta"]["team2TotalAverageSr"] - response.data["api"]["balancerMeta"]["team1TotalAverageSr"];
                team2Meta.push(ref.buildKVPDataRow("Balance Time", response.data["api"]["balancerMeta"]["balanceTime"] + "s"))
                team2Meta.push(ref.buildKVPDataRow("Average SR", response.data["api"]["balancerMeta"]["team2AverageSr"] + " (Δ " + av2Diff + ")"))
                team2Meta.push(ref.buildKVPDataRow("└─ Total SR", response.data["api"]["balancerMeta"]["team2TotalSr"]))
                team2Meta.push(ref.buildKVPDataRow("Average SR (All roles)", response.data["api"]["balancerMeta"]["team2TotalAverageSr"] + " (Δ " + totalAv2Diff + ")"))
                team2Meta.push(ref.buildKVPDataRow("└─ Total SR (All roles)", response.data["api"]["balancerMeta"]["team2TotalSrDistribution"]))
                team2Meta.push(ref.buildKVPDataRow("Adaptability (How well the team can adapt to playing different roles)", response.data["api"]["balancerMeta"]["team2Adaptability"] + "%"))
                team2Meta.push(ref.buildKVPDataRow("├─ Tank Adaptability", response.data["api"]["balancerMeta"]["team2TankAdaptability"] + "%"))
                team2Meta.push(ref.buildKVPDataRow("├─ DPS Adaptability", response.data["api"]["balancerMeta"]["team2DpsAdaptability"] + "%"))
                team2Meta.push(ref.buildKVPDataRow("└─ Support Adaptability", response.data["api"]["balancerMeta"]["team2SupportAdaptability"] + "%"))

                this.setState({
                    table1Rows: team1List,
                    table2Rows: team2List,
                    selectedSelectData: selectedData,
                    table1MetaRows: team1Meta,
                    table2MetaRows: team2Meta
                });
            }
        } catch (e) {
            console.log(e);
            let message = "Invalid request";
            // @ts-ignore
            Notific8.create(message, {themeColor: 'ruby', life: 4000}).then((notification) => {
                // open the notification
                notification.open();
            });
        }
        this.setDisabled(false);
    }

    getPositionForId(id: number) {
        switch (id) {
            case 0:
                return "Tank";
            case 1:
                return "DPS";
            case 2:
                return "Support";
        }
    }

    getPositionSr(position: number, user: Object) {
        switch (position) {
            case 0:
                // @ts-ignore
                return user["tankSr"];
            case 1:
                // @ts-ignore
                return user["dpsSr"];
            case 2:
                // @ts-ignore
                return user["supportSr"];
        }
    }

    call() {
        console.log("call");
    }

    render() {
        let ref = this;
        let dialog = <div/>;
        if(this.state.showAddPlayerDialog) {
            dialog = <AddPlayerDialog onClose={()=>function () {
                ref.setState({showAddPlayerDialog: false})
            }} onUpdate={this.fetchNewUsers} />;
        } else if(this.state.showEditPlayerDialog) {
            dialog = <EditPlayerDialog onClose={()=>function () {
                ref.setState({showEditPlayerDialog: false})
            }} data={this.state.editUserList} onUpdate={this.fetchNewUsers}/>;
        }
        return <div id="parent" className="container container-mod">
            <Select multiple={true} value={this.state.selectedSelectData} id="sl">
                {this.state.selectData}
            </Select>
            <form onSubmit={(e) => this.handleSubmit(e)}>
                <Button id="sb" cls="success form-group form-control" title="Balance" type="submit"/>
            </form>
            <div className="grid m-0">
                <div className="row m-0">
                    <div className="cell-6 p-0 pr-2">
                        <table className="table striped table-border mt-4" data-role="table">
                            <thead>
                            <tr>
                                <th>Role</th>
                                <th>Username</th>
                                <th>Sr</th>
                                <th>Tank</th>
                                <th>Dps</th>
                                <th>Support</th>
                            </tr>
                            </thead>
                            <tbody>
                            {this.state.table1Rows}
                            </tbody>
                        </table>
                    </div>
                    <div className="cell-6 p-0 pl-2">
                        <table className="table striped table-border mt-4" data-role="table">
                            <thead>
                            <tr>
                                <th>Role</th>
                                <th>Username</th>
                                <th>Sr</th>
                                <th>Tank</th>
                                <th>Dps</th>
                                <th>Support</th>
                            </tr>
                            </thead>
                            <tbody>
                            {this.state.table2Rows}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <Dropdown autoClose={false}>
                <button className="button warning outline">Balancer Metadata</button>
                <div className="grid m-0">
                    <div className="row m-0">
                        <div className="cell-6 p-0 pr-2">
                            <table className="table striped table-border mt-4 mb-0" data-role="table">
                                <thead>
                                <tr>
                                    <th>Key</th>
                                    <th>Value</th>
                                </tr>
                                </thead>
                                <tbody>
                                {this.state.table1MetaRows}
                                </tbody>
                            </table>
                        </div>
                        <div className="cell-6 p-0 pl-2">
                            <table className="table striped table-border mt-4 mb-0" data-role="table">
                                <thead>
                                <tr>
                                    <th>Key</th>
                                    <th>Value</th>
                                </tr>
                                </thead>
                                <tbody>
                                {this.state.table2MetaRows}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </Dropdown>
            <MultiAction icon="more-vert" cls="secondary" drop={'up'}>
                <MultiActionItem icon="refresh" onClick={()=>this.fetchNewUsers()}/>
                <MultiActionItem icon="user-plus" onClick={()=>this.openAddUserDialog()}/>
                <MultiActionItem icon="users" onClick={()=>this.openEditUserDialog()}/>
                <MultiActionItem icon="cog" className="disabled"/>
            </MultiAction>
            {dialog}
        </div>;
    }
}

export default Home;
