import React, {FormEvent} from "react";
import axios from "axios";
// @ts-ignore
import {Notific8} from 'notific8';
import "../node_modules/notific8/src/sass/notific8.scss";
// @ts-ignore
import {Button, Dropdown, MultiAction, MultiActionItem, Select} from "metro4-react";
import AddPlayerDialog from "./components/addplayerdialog/AddPlayerDialog";
import EditPlayerDialog from "./components/editplayerdialog/EditPlayerDialog";
import BalanceTablePage from "./models/BalanceTablePage";
import DefaultStyle from "./components/style/DefaultStyle";
import ExportDataDialog from "./components/exportdatadialog/ExportDataDialog";
import ImportDataDialog from "./components/importdatadialog/ImportDataDialog";

type LoginProps = {
    // using `interface` is also ok
};
type LoginState = {
    selectData?: Array<JSX.Element>;
    selectedSelectData: Array<string>;
    tablePages: Array<BalanceTablePage>;
    selectedPage: number;
    showAddPlayerDialog?: boolean;
    showEditPlayerDialog?: boolean;
    showExportDataDialog?: boolean;
    showImportDataDialog?: boolean
    editUserList?: Array<JSX.Element>;
    style: string;
};

class Home extends React.Component<LoginProps, LoginState> {


    constructor(props: LoginProps) {
        super(props);

        let tablePage = new Array<BalanceTablePage>();
        tablePage.push(new BalanceTablePage(this.initTable(), new Array<JSX.Element>(), this.initTable(), new Array<JSX.Element>()));
        this.state = {
            selectData: new Array<JSX.Element>(),
            selectedSelectData: new Array<string>(),
            tablePages: tablePage,
            selectedPage: 0,
            showAddPlayerDialog: false,
            showEditPlayerDialog: false,
            showExportDataDialog: false,
            showImportDataDialog: false,
            editUserList: new Array<JSX.Element>(),
            style: "default"
        };

        this.handleSubmit = this.handleSubmit.bind(this);
        this.fetchNewUsers = this.fetchNewUsers.bind(this);
        this.openAddUserDialog = this.openAddUserDialog.bind(this);
        this.openEditUserDialog = this.openEditUserDialog.bind(this);
        this.openExportDataDialog = this.openExportDataDialog.bind(this);
        this.openImportDataDialog = this.openImportDataDialog.bind(this);
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

    buildMultiDataRow(position: String, name: String, sr: String, te: number, de: number, se: number, warn: number) {
        return (
            <tr className={(warn > 150 ? "bg-darkRed" : warn > 120 ? "bg-orange" : "")}>
                <td>{position}</td>
                <td>{name}</td>
                <td>{sr}</td>
                <td><span
                    className={te == 0 ? "mif-not fg-red" : te == 1 ? "mif-checkmark fg-cobalt" : "mif-checkmark fg-green"}/>
                </td>
                <td><span
                    className={de == 0 ? "mif-not fg-red" : de == 1 ? "mif-checkmark fg-cobalt" : "mif-checkmark fg-green"}/>
                </td>
                <td><span
                    className={se == 0 ? "mif-not fg-red" : se == 1 ? "mif-checkmark fg-cobalt" : "mif-checkmark fg-green"}/>
                </td>
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

    openExportDataDialog() {
        this.setState({showExportDataDialog: true});
    }

    openImportDataDialog() {
        this.setState({showImportDataDialog: true});
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
                let idMap = new Map<number, number>();
                let tList = new Array<string>();
                response.data["api"]["users"].forEach(function (value: Object) {
                    // @ts-ignore
                    let id = value["id"];
                    // @ts-ignore
                    editUserList.push(<option value={id}>{value["discordName"]}</option>)
                    // @ts-ignore
                    value["owNames"].forEach(function (value: Object) {
                        let idNum = idMap.get(id);
                        if(idNum !== undefined) {
                            idNum += 1;
                        } else {
                            idNum = 0;
                        }
                        // @ts-ignore
                        mm.push(<option value={idNum + "#" + id}>{value}</option>);
                        idMap.set(id, idNum);
                        tList.push(idNum + "#" + id);
                    });
                });
                if (notify) {
                    let message = "User list updated";
                    // @ts-ignore
                    Notific8.create(message, {themeColor: 'lime', life: 4000}).then((notification) => {
                        // open the notification
                        notification.open();
                    });
                }
                let selData = new Array<string>();
                sState.state.selectedSelectData.forEach(function (item) {
                    if(tList.indexOf(item) !== -1) {
                        selData.push(item);
                    }
                });

                sState.setState({selectData: mm, editUserList: editUserList, selectedSelectData: selData});
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
                selectedPage: 0
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
        let selectedDataNameMap = new Map<number, string>();
        // @ts-ignore
        for (let option of document.getElementsByTagName("select")[0].options) {
            if (option.selected) {
                selectedData.push(option.value);
                let id = Number.parseInt(option.value.split("#")[1]);
                selected.add(id);
                selectedDataNameMap.set(id, option.text);
            }
        }
        try {
            const response = await axios.post("/api/v1/balance", {
                userIds: Array.from(selected),
            }, {
                responseType: "json",
            });
            if (response.status === 200) {
                let dataList = new Array<BalanceTablePage>();
                dataList.push(new BalanceTablePage(this.initTable(), new Array<JSX.Element>(), this.initTable(), new Array<JSX.Element>()));
                response.data["api"]["userList"].forEach(function (valList: Array<any>, index1: number) {
                    let team1List = new Array<JSX.Element>();
                    let team2List = new Array<JSX.Element>();
                    valList.forEach(function (value: Object, index: number) {
                        // @ts-ignore
                        let position = Number.parseInt(value["position"]);
                        // @ts-ignore
                        let tpf = value["user"]["tankPreference"];
                        // @ts-ignore
                        let dpf = value["user"]["dpsPreference"];
                        // @ts-ignore
                        let spf = value["user"]["supportPreference"];
                        // @ts-ignore
                        let sr = ref.getPositionSr(position, value["user"]);
                        // @ts-ignore
                        if (Number.parseInt(value["team"]) === 1) {
                            let avg = (sr * 100.0) / response.data["api"]["balancerMeta"][index1]["team1AverageSr"];
                            // @ts-ignore
                            team1List.push(ref.buildMultiDataRow(ref.getPositionForId(position), selectedDataNameMap.get(value["user"]["id"]), sr, tpf, dpf, spf, avg));
                        } else {
                            let avg = (sr * 100.0) / response.data["api"]["balancerMeta"][index1]["team2AverageSr"];
                            // @ts-ignore
                            team2List.push(ref.buildMultiDataRow(ref.getPositionForId(position), selectedDataNameMap.get(value["user"]["id"]), sr, tpf, dpf, spf, avg));
                        }
                    });
                    while (team1List.length < 6) {
                        team1List.push(ref.buildBlankTableRow());
                    }
                    while (team2List.length < 6) {
                        team2List.push(ref.buildBlankTableRow());
                    }
                    dataList.push(new BalanceTablePage(team1List, new Array<JSX.Element>(), team2List, new Array<JSX.Element>()))
                });
                response.data["api"]["balancerMeta"].forEach(function (val: any, index: number) {
                    let team1Meta = new Array<JSX.Element>();
                    let team2Meta = new Array<JSX.Element>();
                    // stats team 1
                    let av1Diff = val["team1AverageSr"] - val["team2AverageSr"];
                    let totalAv1Diff = val["team1TotalAverageSr"] - val["team2TotalAverageSr"];
                    team1Meta.push(ref.buildKVPDataRow("Balance Score", val["balanceScore"]))
                    team1Meta.push(ref.buildKVPDataRow("Average SR", val["team1AverageSr"] + " (Δ " + av1Diff + ")"))
                    team1Meta.push(ref.buildKVPDataRow("└─ Total SR", val["team1TotalSr"]))
                    team1Meta.push(ref.buildKVPDataRow("Average SR (All roles)", val["team1TotalAverageSr"] + " (Δ " + totalAv1Diff + ")"))
                    team1Meta.push(ref.buildKVPDataRow("└─ Total SR (All roles)", val["team1TotalSrDistribution"]))
                    team1Meta.push(ref.buildKVPDataRow("Adaptability (How well the team can adapt to playing different roles)", val["team1Adaptability"] + "%"))
                    team1Meta.push(ref.buildKVPDataRow("├─ Tank Adaptability", val["team1TankAdaptability"] + "%"))
                    team1Meta.push(ref.buildKVPDataRow("├─ DPS Adaptability", val["team1DpsAdaptability"] + "%"))
                    team1Meta.push(ref.buildKVPDataRow("└─ Support Adaptability", val["team1SupportAdaptability"] + "%"))
                    // stats team 2
                    let av2Diff = val["team2AverageSr"] - val["team1AverageSr"];
                    let totalAv2Diff = val["team2TotalAverageSr"] - val["team1TotalAverageSr"];
                    team2Meta.push(ref.buildKVPDataRow("Balance Time", val["balanceTime"] + "s"))
                    team2Meta.push(ref.buildKVPDataRow("Average SR", val["team2AverageSr"] + " (Δ " + av2Diff + ")"))
                    team2Meta.push(ref.buildKVPDataRow("└─ Total SR", val["team2TotalSr"]))
                    team2Meta.push(ref.buildKVPDataRow("Average SR (All roles)", val["team2TotalAverageSr"] + " (Δ " + totalAv2Diff + ")"))
                    team2Meta.push(ref.buildKVPDataRow("└─ Total SR (All roles)", val["team2TotalSrDistribution"]))
                    team2Meta.push(ref.buildKVPDataRow("Adaptability (How well the team can adapt to playing different roles)", val["team2Adaptability"] + "%"))
                    team2Meta.push(ref.buildKVPDataRow("├─ Tank Adaptability", val["team2TankAdaptability"] + "%"))
                    team2Meta.push(ref.buildKVPDataRow("├─ DPS Adaptability", val["team2DpsAdaptability"] + "%"))
                    team2Meta.push(ref.buildKVPDataRow("└─ Support Adaptability", val["team2SupportAdaptability"] + "%"))

                    dataList[index + 1].table1MetaRows = team1Meta;
                    dataList[index + 1].table2MetaRows = team2Meta;
                });

                this.setState({
                    selectedSelectData: selectedData,
                    tablePages: dataList,
                    selectedPage: 1
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

    changeTablePage(val: number) {
        if (val < this.state.tablePages.length && val > 0) {
            this.setState({selectedPage: val});
        }
    }

    render() {
        let ref = this;
        let dialog = <div/>;
        let stylea;
        if (this.state.style === "default") {
            stylea = <DefaultStyle/>
        } else {
            stylea = <DefaultStyle/>
        }
        if (this.state.showAddPlayerDialog) {
            dialog = <AddPlayerDialog onClose={() => function () {
                ref.setState({showAddPlayerDialog: false})
            }} onUpdate={this.fetchNewUsers}/>;
        } else if (this.state.showEditPlayerDialog) {
            dialog = <EditPlayerDialog onClose={() => function () {
                ref.setState({showEditPlayerDialog: false})
            }} data={this.state.editUserList} onUpdate={this.fetchNewUsers}/>;
        } else if (this.state.showExportDataDialog) {
            dialog = <ExportDataDialog onClose={() => function () {
                ref.setState({showExportDataDialog: false})
            }}/>;
        } else if (this.state.showImportDataDialog) {
            dialog = <ImportDataDialog onClose={() => function () {
                ref.setState({showExportDataDialog: false})
            }} onUpdate={this.fetchNewUsers}/>;
        }
        return <div id="parent" className="container container-mod">
            {stylea}
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
                            {this.state.tablePages[this.state.selectedPage].table1Rows}
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
                            {this.state.tablePages[this.state.selectedPage].table2Rows}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <ul className="pagination">
                <li className={"page-item service prev-page " + (this.state.selectedPage === 1 ? "disabled" : "")}><a
                    className="page-link"
                    onClick={() => this.changeTablePage(this.state.selectedPage - 1)}>Prev</a>
                </li>
                <li className={"page-item " + (this.state.selectedPage === 1 ? "active" : "")}><a className="page-link"
                                                                                                  onClick={() => this.changeTablePage(1)}>1</a>
                </li>
                <li className={"page-item " + (this.state.selectedPage === 2 ? "active" : "")}><a className="page-link"
                                                                                                  onClick={() => this.changeTablePage(2)}>2</a>
                </li>
                <li className={"page-item " + (this.state.selectedPage === 3 ? "active" : "")}><a className="page-link"
                                                                                                  onClick={() => this.changeTablePage(3)}>3</a>
                </li>
                <li className={"page-item " + (this.state.selectedPage === 4 ? "active" : "")}><a className="page-link"
                                                                                                  onClick={() => this.changeTablePage(4)}>4</a>
                </li>
                <li className={"page-item " + (this.state.selectedPage === 5 ? "active" : "")}><a className="page-link"
                                                                                                  onClick={() => this.changeTablePage(5)}>5</a>
                </li>
                <li className={"page-item service next-page " + (this.state.selectedPage === 5 ? "disabled" : "")}><a
                    className="page-link"
                    onClick={() => this.changeTablePage(this.state.selectedPage + 1)}>Next</a>
                </li>
            </ul>
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
                                {this.state.tablePages[this.state.selectedPage].table1MetaRows}
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
                                {this.state.tablePages[this.state.selectedPage].table2MetaRows}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </Dropdown>
            <MultiAction icon="more-vert" cls="secondary" drop={'up'}>
                <MultiActionItem icon="refresh" onClick={() => this.fetchNewUsers()}/>
                <MultiActionItem icon="user-plus" onClick={() => this.openAddUserDialog()}/>
                <MultiActionItem icon="users" onClick={() => this.openEditUserDialog()}/>
                <MultiActionItem icon="file-download" onClick={() => this.openExportDataDialog()}/>
                <MultiActionItem icon="file-upload" onClick={() => this.openImportDataDialog()}/>
            </MultiAction>
            {dialog}
        </div>;
    }
}

export default Home;
